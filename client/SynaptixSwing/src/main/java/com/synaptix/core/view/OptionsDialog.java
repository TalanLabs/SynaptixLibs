package com.synaptix.core.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.ValidationResult;
import com.synaptix.core.CoreMessages;
import com.synaptix.core.event.ViewOptionListener;
import com.synaptix.core.event.ViewOptionStateEvent;
import com.synaptix.core.option.IViewOption;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.widget.AbstractAcceptAction;
import com.synaptix.swing.widget.AbstractCancelAction;

public class OptionsDialog extends JPanel {

	private static final long serialVersionUID = 2793519971230982691L;

	public final static int ACCEPT_OPTION = 0;

	public final static int CANCEL_OPTION = 1;

	private final static String TEXT_TITLE = CoreMessages.getString("OptionsDialog.0"); //$NON-NLS-1$

	private final static Icon toolsOption;

	private JDialogModel dialog;

	private int returnValue;

	private Action acceptAction;

	private Action cancelAction;

	private Action restaureDefaultsAction;

	private Action applyAction;

	private OptionsListModel optionsListModel;

	private JList optionsList;

	private JLabel titleLabel;

	private CardLayout cardLayout;

	private JPanel cardPanel;

	private JPanel buttonsPanel;

	private IViewOption currentViewOption;

	static {
		toolsOption = new ImageIcon(OptionsDialog.class.getResource("/images/options/toolsOption.png")); //$NON-NLS-1$
	}

	public OptionsDialog() {
		super(new BorderLayout());

		initActions();
		initComponents();

		dialog = null;
		this.add(buildEditorPanel(), BorderLayout.CENTER);
	}

	private void initActions() {
		acceptAction = new AcceptAction();
		cancelAction = new CancelAction();

		restaureDefaultsAction = new RestaureDefaultsAction();
		applyAction = new ApplyAction();
	}

	private void createComponents() {
		optionsListModel = new OptionsListModel();
		optionsList = new JList(optionsListModel);
		titleLabel = new JLabel();

		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);

		buttonsPanel = ButtonBarFactory.buildLeftAlignedBar(new JButton(restaureDefaultsAction), new JButton(applyAction));
	}

	private void initComponents() {
		createComponents();

		titleLabel.setFont(new Font("Arial", Font.BOLD, 15)); //$NON-NLS-1$
		titleLabel.setBorder(BorderFactory.createEtchedBorder());

		optionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		optionsList.setCellRenderer(new OptionsListCellRenderer());
		optionsList.getSelectionModel().addListSelectionListener(new OptionsListSelectionListener());
		optionsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		optionsList.setVisibleRowCount(1);

		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 4));
	}

	private JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("FILL:500DLU:GROW(1.0)", //$NON-NLS-1$
				"FILL:50DLU:NONE,FILL:30DLU:NONE,FILL:200DLU:GROW(1.0),FILL:DEFAULT:NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		JScrollPane scrollPane = new JScrollPane(optionsList);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		builder.add(scrollPane, cc.xywh(1, 1, 1, 1));
		builder.add(titleLabel, cc.xyw(1, 2, 1));
		builder.add(cardPanel, cc.xyw(1, 3, 1));
		builder.add(buttonsPanel, cc.xy(1, 4));
		return builder.getPanel();
	}

	public int showDialog(Component parent, List<IViewOption> viewOptions, String id) {
		returnValue = CANCEL_OPTION;

		optionsListModel.setViewPreferences(viewOptions);

		ViewOptionListener viewOptionListener = new MyViewOptionListener();

		for (IViewOption viewOption : viewOptions) {
			viewOption.addViewOptionListener(viewOptionListener);

			cardPanel.add(new JScrollPane(viewOption.getView()), viewOption.getId());
		}

		IViewOption find = null;
		if (id != null) {
			for (IViewOption viewOption : viewOptions) {
				if (viewOption.getId().equals(id)) {
					find = viewOption;
				}
			}
		}
		if (find != null) {
			optionsList.setSelectedValue(find, true);
		} else {
			optionsList.setSelectedIndex(0);
		}

		dialog = new JDialogModel(parent, TEXT_TITLE, null, this, new Action[] { acceptAction, cancelAction }, new OpenActionListener(), cancelAction);

		dialog.setResizable(true);

		dialog.showDialog();
		dialog.dispose();

		for (IViewOption viewOption : viewOptions) {
			viewOption.removeViewOptionListener(viewOptionListener);
		}

		fireClosedViewOptions();

		return returnValue;
	}

	private void fireOpenedViewOptions() {
		ViewOptionStateEvent event = new ViewOptionStateEvent(this, ViewOptionStateEvent.State.OPENED, false);
		for (IViewOption viewOption : optionsListModel.getViewPreferences()) {
			viewOption.viewOptionStateChanged(event);
		}
	}

	private void fireShowViewOption(IViewOption viewOption) {
		ViewOptionStateEvent event = new ViewOptionStateEvent(this, ViewOptionStateEvent.State.SHOW, false);
		viewOption.viewOptionStateChanged(event);
	}

	private void fireClosedViewOptions() {
		ViewOptionStateEvent event = new ViewOptionStateEvent(this, ViewOptionStateEvent.State.CLOSED, false);
		for (IViewOption viewOption : optionsListModel.getViewPreferences()) {
			viewOption.viewOptionStateChanged(event);
		}
	}

	private void fireHiddenViewOption(IViewOption viewOption) {
		ViewOptionStateEvent event = new ViewOptionStateEvent(this, ViewOptionStateEvent.State.HIDDEN, false);
		viewOption.viewOptionStateChanged(event);
	}

	private final class OpenActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			fireOpenedViewOptions();
		}
	}

	private final class OptionsListSelectionListener implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				if (currentViewOption != null) {
					fireHiddenViewOption(currentViewOption);
				}

				IViewOption viewOption = (IViewOption) optionsList.getSelectedValue();

				titleLabel.setText(viewOption.getName());
				cardLayout.show(cardPanel, viewOption.getId());

				fireShowViewOption(viewOption);

				currentViewOption = viewOption;
			}
		}
	}

	private final class AcceptAction extends AbstractAcceptAction {

		private static final long serialVersionUID = 8317354110381792022L;

		public void actionPerformed(ActionEvent e) {
			returnValue = ACCEPT_OPTION;
			dialog.closeDialog();
		}
	}

	private final class CancelAction extends AbstractCancelAction {

		private static final long serialVersionUID = 8317354110381792022L;

		public void actionPerformed(ActionEvent e) {
			returnValue = CANCEL_OPTION;
			dialog.closeDialog();
		}
	}

	private final class RestaureDefaultsAction extends AbstractAction {

		private static final long serialVersionUID = 8317354110381792022L;

		public RestaureDefaultsAction() {
			super(CoreMessages.getString("OptionsDialog.7")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			IViewOption viewPreference = (IViewOption) optionsList.getSelectedValue();
			if (viewPreference != null) {
				viewPreference.restoreDefault();
			}
		}
	}

	private final class ApplyAction extends AbstractAction {

		private static final long serialVersionUID = 8317354110381792022L;

		public ApplyAction() {
			super(CoreMessages.getString("OptionsDialog.8")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			IViewOption viewPreference = (IViewOption) optionsList.getSelectedValue();
			if (viewPreference != null) {
				viewPreference.apply();
			}
		}
	}

	private final class OptionsListModel extends AbstractListModel {

		private static final long serialVersionUID = -3042050509600639806L;

		private List<IViewOption> viewPreferences;

		public void setViewPreferences(List<IViewOption> viewPreferences) {
			this.viewPreferences = viewPreferences;

			fireIntervalAdded(this, 0, viewPreferences.size());
		}

		public List<IViewOption> getViewPreferences() {
			return viewPreferences;
		}

		public int getSize() {
			if (viewPreferences != null) {
				return viewPreferences.size();
			}
			return 0;
		}

		public Object getElementAt(int index) {
			return viewPreferences.get(index);
		}
	}

	private final class OptionsListCellRenderer extends JLabel implements ListCellRenderer {

		private static final long serialVersionUID = -3686539449199414414L;

		public OptionsListCellRenderer() {
			super();
			this.setOpaque(true);
			this.setHorizontalAlignment(JLabel.CENTER);
			this.setHorizontalTextPosition(JLabel.CENTER);
			this.setVerticalTextPosition(JLabel.BOTTOM);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			if (value != null && value instanceof IViewOption) {
				IViewOption viewOption = (IViewOption) value;
				Icon icon = viewOption.getIcon() != null ? viewOption.getIcon() : toolsOption;

				this.setText(viewOption.getName());
				this.setIcon(icon);
			} else {
				this.setText(null);
				this.setIcon(null);
			}

			return this;
		}
	}

	private final class MyViewOptionListener implements ViewOptionListener {

		public void viewOptionValidationResultChanged(IViewOption viewOption, ValidationResult result) {
			acceptAction.setEnabled(result.isEmpty());
			applyAction.setEnabled(result.isEmpty());
			optionsList.setEnabled(result.isEmpty());
		}
	}
}