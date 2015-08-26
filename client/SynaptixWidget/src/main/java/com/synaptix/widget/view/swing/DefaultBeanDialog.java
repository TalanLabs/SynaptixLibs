package com.synaptix.widget.view.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.pushingpixels.flamingo.api.common.JScrollablePanel;
import org.pushingpixels.flamingo.api.common.JScrollablePanel.ScrollType;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.synaptix.client.view.IView;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.utils.GUIWindow;
import com.synaptix.widget.actions.view.swing.AbstractAcceptAction;
import com.synaptix.widget.actions.view.swing.AbstractCancelAction;
import com.synaptix.widget.actions.view.swing.AbstractCloseAction;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.dialog.BeanValidatorListener;
import com.synaptix.widget.view.dialog.IBeanDialogView;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;
import com.synaptix.widget.view.swing.dialog.DefaultBeanExtensionDialogCellRenderer;

public class DefaultBeanDialog<E> extends WaitComponentFeedbackPanel implements IBeanDialogView<E> {

	private static final long serialVersionUID = 2124940929569388267L;

	public static final String KEEP_OPENED = "keepOpened";

	private final Map<IBeanExtensionDialogView<E>, ValidationResult> validatorMap;

	protected E bean;

	protected Map<String, Object> valueMap;

	private JDialogModel dialog;

	private Action acceptAction;

	private Action cancelAction;

	private Action closeAction;

	protected int returnValue;

	private boolean hideListIfAlone;

	protected List<IBeanExtensionDialogView<E>> beanExtensionDialogs;

	private DefaultListModel beanExtensionDialogListModel;

	protected JList list;

	protected JScrollablePanel<JList> listScrollPane;

	protected CardLayout cardLayout;

	protected JPanel cardPanel;

	protected JPanel allPanel;

	private ValidationResultModel validationResultModel;

	private boolean useCardLayout;

	private BeanExtensionDialogCellRenderer<E> beanExtensionDialogCellRenderer = new DefaultBeanExtensionDialogCellRenderer<E>();

	protected boolean readOnly;

	private String id;

	protected String acceptActionLabel;

	protected String cancelActionLabel;

	protected String closeActionLabel;

	private boolean acceptActionEnabled = false;

	private boolean creation;

	public DefaultBeanDialog(IBeanExtensionDialogView<E>... beanExtensionDialogs) {
		this(true, beanExtensionDialogs);
	}

	public DefaultBeanDialog(boolean hideListIfAlone, IBeanExtensionDialogView<E>... beanExtensionDialogs) {
		this(hideListIfAlone, false, null, null, null, beanExtensionDialogs);
	}

	public DefaultBeanDialog(boolean hideListIfAlone, boolean acceptActionEnabled, String acceptActionLabel, String cancelActionLabel, String closeActionLabel,
			IBeanExtensionDialogView<E>... beanExtensionDialogs) {
		super();

		this.acceptActionLabel = acceptActionLabel;
		this.cancelActionLabel = cancelActionLabel;
		this.closeActionLabel = closeActionLabel;
		this.acceptActionEnabled = acceptActionEnabled;

		if (beanExtensionDialogs == null || beanExtensionDialogs.length == 0) {
			throw new IllegalArgumentException("beanExtensionDialogs must is not null");
		}

		this.hideListIfAlone = hideListIfAlone;
		this.beanExtensionDialogs = new ArrayList<IBeanExtensionDialogView<E>>(Arrays.asList(beanExtensionDialogs));

		this.validatorMap = new HashMap<IBeanExtensionDialogView<E>, ValidationResult>();

		for (IBeanExtensionDialogView<E> extensionDialogView : this.beanExtensionDialogs) {
			extensionDialogView.setBeanDialog(this);
		}

		initComponents();

		this.addContent(buildContents());
	}

	private void initComponents() {
		if (acceptActionLabel != null) {
			acceptAction = new AcceptAction(acceptActionLabel);
		} else {
			acceptAction = new AcceptAction();
		}
		if (cancelActionLabel != null) {
			cancelAction = new CancelAction(cancelActionLabel);
		} else {
			cancelAction = new CancelAction();
		}
		if (closeActionLabel != null) {
			closeAction = new CloseAction(closeActionLabel);
		} else {
			closeAction = new CloseAction();
		}

		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);

		beanExtensionDialogListModel = new DefaultListModel();
		list = createList(beanExtensionDialogListModel);
		listScrollPane = createListScrollPane(list);

		allPanel = new JPanel(new BorderLayout());

		validationResultModel = new DefaultValidationResultModel();
	}

	protected JList createList(ListModel listModel) {
		JList list = new JList(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new BeanExtensionDialogListCellRenderer());
		list.getSelectionModel().addListSelectionListener(new BeanExtensionDialogListSelectionListener());
		return list;
	}

	protected JScrollablePanel<JList> createListScrollPane(JList list) {
		JScrollablePanel<JList> pageScrollablePanel = new JScrollablePanel<JList>(list, ScrollType.VERTICALLY);
		// JArrowScrollPane arrowScrollPane = new JArrowScrollPane(list);
		// arrowScrollPane.setBorder(BorderFactory.createEmptyBorder());
		return pageScrollablePanel;
	}

	private JComponent buildContents() {
		return allPanel;
	}

	protected Component buildSimplePanel() {
		return getComponent(beanExtensionDialogs.get(0));
	}

	protected JComponent buildAllPanel() {
		FormLayout layout = new FormLayout("FILL:75DLU:NONE,FILL:DEFAULT:GROW(1.0)", //$NON-NLS-1$
				"FILL:DEFAULT:GROW(1.0)"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(listScrollPane, cc.xy(1, 1));
		builder.add(cardPanel, cc.xy(2, 1));
		return builder.getPanel();
	}

	@Override
	public int showDialog(IView parent, String title, String subtitle, E bean, Map<String, Object> valueMap, boolean readOnly, boolean creation) {
		return showDialog(parent, title, subtitle, bean, valueMap, readOnly, creation, false);
	}

	@Override
	public int showDialog(IView parent, String title, String subtitle, E bean, Map<String, Object> valueMap, boolean readOnly, boolean creation, final boolean acceptAndReopenOption) {
		this.returnValue = CANCEL_OPTION;
		this.bean = bean;
		this.valueMap = valueMap;
		this.readOnly = readOnly;
		this.creation = creation;

		cardPanel.removeAll();
		allPanel.removeAll();

		StringBuilder sb = new StringBuilder();
		if (parent != null) {
			sb.append(parent.getClass().getName());
			sb.append("_");
		}
		sb.append(title).append("_").append(subtitle);
		for (IBeanExtensionDialogView<E> e : beanExtensionDialogs) {
			sb.append(e.getClass().getName());
			sb.append("_");
		}
		this.id = sb.toString();

		useCardLayout = !(hideListIfAlone && beanExtensionDialogs.size() == 1);

		MyValidatorListener vl = new MyValidatorListener();
		for (int i = 0; i < beanExtensionDialogs.size(); i++) {
			IBeanExtensionDialogView<E> b = beanExtensionDialogs.get(i);
			b.addValidatorListener(vl);

			if (useCardLayout) {
				cardPanel.add(getComponent(b), String.valueOf(i));
			}
		}

		if (useCardLayout) {
			allPanel.add(buildAllPanel(), BorderLayout.CENTER);
		} else {
			allPanel.add(buildSimplePanel(), BorderLayout.CENTER);
		}

		if (!readOnly && !showCloseOnly()) {
			List<Action> actionTabList = buildActionTab();
			if (acceptAndReopenOption) {
				final AcceptAndReopenAction acceptAndReopenAction = new AcceptAndReopenAction();
				acceptAction.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if ("enabled".equals(evt.getPropertyName())) { //$NON-NLS-1$
							acceptAndReopenAction.setEnabled((Boolean) evt.getNewValue());
						}
					}
				});
				actionTabList.add(0, acceptAndReopenAction);
			}
			Action[] actionTab = actionTabList.toArray(new Action[actionTabList.size()]);
			if (getOthersActions().length != 0) {

				List<Action> actionList = new ArrayList<Action>();
				actionList.add(acceptAction);
				actionList.add(cancelAction);
				for (Action a : getOthersActions()) {
					actionList.add(a);
					// a.setEnabled(false);
				}
				actionTab = actionList.toArray(new Action[actionList.size()]);
			}

			dialog = new JDialogModel(getComponent(parent), title, subtitle, this, actionTab, new OpenActionListener(), cancelAction);
		} else {
			dialog = new JDialogModel(getComponent(parent), title, subtitle, this, new Action[] { closeAction }, new OpenActionListener(), closeAction);
		}

		dialog.setId(id);
		acceptAction.setEnabled(acceptActionEnabled);
		dialog.setResizable(true);

		dialog.showDialog();
		dialog.dispose();
		dialog = null;

		for (int i = 0; i < beanExtensionDialogs.size(); i++) {
			IBeanExtensionDialogView<E> b = beanExtensionDialogs.get(i);
			b.removeValidatorListener(vl);

			cardPanel.remove(getComponent(b));
		}

		return returnValue;
	}

	protected Action[] getOthersActions() {
		return new Action[] {};
	}

	protected boolean showCloseOnly() {
		return false;
	}

	/**
	 * Build the action tab
	 *
	 * @return
	 */
	protected List<Action> buildActionTab() {
		return CollectionHelper.asListOf(Action.class, acceptAction, cancelAction);
	}

	protected final Action getAcceptAction() {
		return acceptAction;
	}

	protected Component getComponent(IView parent) {
		if (parent instanceof Component) {
			return (Component) parent;
		}
		return GUIWindow.getActiveWindow();
	}

	public final ValidationResultModel getValidationResultModel() {
		return validationResultModel;
	}

	@Override
	public void setTitle(String title) {
		if (dialog != null) {
			dialog.setTitle(title);
		}
	}

	@Override
	public void setSubtitle(String subtitle) {
		if (dialog != null) {
			dialog.setSubTitle(subtitle);
		}
	}

	@Override
	public E getBean() {
		return bean;
	}

	@Override
	public Map<String, Object> getValueMap() {
		return valueMap;
	}

	private void updateValidator() {
		ValidationResult result = new ValidationResult();
		for (ValidationResult r : validatorMap.values()) {
			result.addAllFrom(r);
		}
		validationResultModel.setResult(result);
		acceptAction.setEnabled(!result.hasErrors());
	}

	@Override
	public void closeDialog() {
		for (IBeanExtensionDialogView<E> b : beanExtensionDialogs) {
			b.closeDialog();
		}

		dialog.closeDialog();
	}

	@Override
	public void accept(boolean close) {
		if (close) {
			acceptAction.actionPerformed(null);
		} else {
			acceptAction.actionPerformed(new ActionEvent(acceptAction, 0, KEEP_OPENED));
		}
	}

	private final class OpenActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (readOnly) {
				final JButton button = dialog.getButton(closeAction);
				if (button != null) {
					button.requestFocus();
				}
			} else {
				final JButton button = dialog.getButton(cancelAction);
				if (button != null) {
					button.requestFocus();
				}
			}

			fillListModel();

			for (IBeanExtensionDialogView<E> b : beanExtensionDialogs) {
				b.setBean(bean, valueMap, readOnly, creation);
			}

			for (IBeanExtensionDialogView<E> b : beanExtensionDialogs) {
				b.openDialog();
			}

			if (list.getSelectedIndex() == -1) {
				list.setSelectedIndex(0);
			}

			openDialog();
		}
	}

	/**
	 * Called upon dialog opening
	 */
	protected void openDialog() {
	}

	protected void fillListModel() {
		beanExtensionDialogListModel.clear();
		for (IBeanExtensionDialogView<E> ex : beanExtensionDialogs) {
			beanExtensionDialogListModel.addElement(ex);
		}
	}

	private final class AcceptAction extends AbstractAcceptAction {

		private static final long serialVersionUID = -8520108575061780844L;

		public AcceptAction(String label) {
			super(label);
		}

		public AcceptAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			returnValue = ACCEPT_OPTION;

			for (IBeanExtensionDialogView<E> b : beanExtensionDialogs) {
				b.commit(bean, valueMap);
			}

			boolean close = e == null || e.getActionCommand() != KEEP_OPENED || closeOnAccept();
			if (close) {
				closeDialog();
			}

			doSave(close);
		}
	}

	/**
	 * The user clicked on the accept button. Here is the implementation of the save part, if not done elsewhere<br/>
	 * By default, does nothing, let the caller of the dialog decide what to do
	 *
	 * @param close
	 *            - The user asked the dialog to be closed
	 */
	protected void doSave(boolean close) {
	}

	private final class CancelAction extends AbstractCancelAction {

		private static final long serialVersionUID = 3844373143848992876L;

		public CancelAction(String label) {
			super(label);
		}

		public CancelAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			returnValue = CANCEL_OPTION;
			closeDialog();
		}
	}

	private final class CloseAction extends AbstractCloseAction {

		private static final long serialVersionUID = 3083786158756879778L;

		public CloseAction(String label) {
			super(label);
		}

		public CloseAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			returnValue = CANCEL_OPTION;
			closeDialog();
		}
	}

	private final class AcceptAndReopenAction extends AbstractAcceptAction {

		private static final long serialVersionUID = -8567759148468177853L;

		public AcceptAndReopenAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().okAndReopen());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			returnValue = ACCEPT_AND_REOPEN_OPTION;

			for (IBeanExtensionDialogView<E> b : beanExtensionDialogs) {
				b.commit(bean, valueMap);
			}

			closeDialog();
		}
	}

	public interface BeanExtensionDialogCellRenderer<T> {

		public Component getCellRendererComponent(DefaultBeanDialog<T> beanDialog, IBeanExtensionDialogView<T> led, ValidationResult result, boolean isSelected);

	}

	protected final class BeanExtensionDialogListCellRenderer implements ListCellRenderer {

		@Override
		@SuppressWarnings("unchecked")
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			IBeanExtensionDialogView<E> led = (IBeanExtensionDialogView<E>) value;
			Component c = beanExtensionDialogCellRenderer.getCellRendererComponent(DefaultBeanDialog.this, led, validatorMap.get(led), isSelected);
			c.setPreferredSize(new Dimension(list.getWidth(), list.getWidth()));
			return c;
		}
	}

	protected final class BeanExtensionDialogListSelectionListener implements ListSelectionListener {

		@Override
		@SuppressWarnings("unchecked")
		public void valueChanged(ListSelectionEvent e) {
			if (useCardLayout && !e.getValueIsAdjusting()) {
				IBeanExtensionDialogView<E> b = (IBeanExtensionDialogView<E>) list.getSelectedValue();

				// TODO Grosse verrue pour corriger le bug de refresh sur les
				// tableaux
				cardPanel.removeAll();
				cardPanel.add(getComponent(b), "toto"); //$NON-NLS-1$
				cardPanel.revalidate();
				cardPanel.repaint();
				cardLayout.show(cardPanel, "toto"); //$NON-NLS-1$

				// cardLayout.show(cardPanel, b.getId());
			}
		}
	}

	private final class MyValidatorListener implements BeanValidatorListener<E> {

		@Override
		public void updateValidator(IBeanExtensionDialogView<E> source, ValidationResult result) {
			validatorMap.put(source, result);

			DefaultBeanDialog.this.updateValidator();
			int index = beanExtensionDialogListModel.indexOf(source);
			beanExtensionDialogListModel.set(index, source);
		}
	}

	/**
	 * When saving, should the dialog be closed?
	 */
	protected boolean closeOnAccept() {
		return true;
	}
}
