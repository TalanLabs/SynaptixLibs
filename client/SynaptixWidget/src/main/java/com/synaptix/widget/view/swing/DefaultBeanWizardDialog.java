package com.synaptix.widget.view.swing;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.ValidationResult;
import com.synaptix.client.view.IView;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.utils.GUIWindow;
import com.synaptix.widget.actions.view.swing.AbstractAcceptAction;
import com.synaptix.widget.actions.view.swing.AbstractCancelAction;
import com.synaptix.widget.actions.view.swing.AbstractNextAction;
import com.synaptix.widget.actions.view.swing.AbstractPreviousAction;
import com.synaptix.widget.pathbar.JPathBar;
import com.synaptix.widget.pathbar.PathBarCellRenderer;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.dialog.BeanValidatorListener;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;
import com.synaptix.widget.view.dialog.IBeanWizardDialogView;

public class DefaultBeanWizardDialog<E> extends WaitComponentFeedbackPanel implements IBeanWizardDialogView<E> {

	private static final long serialVersionUID = 2124940929569388267L;

	private final Map<IBeanExtensionDialogView<E>, ValidationResult> validatorMap;

	protected E bean;

	protected Map<String, Object> valueMap;

	private JDialogModel dialog;

	private Action finishAction;

	private Action cancelAction;

	private Action previousAction;

	private Action nextAction;

	protected int returnValue;

	protected List<IBeanExtensionDialogView<E>> beanExtensionDialogs;

	private DefaultListModel beanExtensionDialogListModel;

	private JPathBar pathBar;

	private CardLayout cardLayout;

	private JPanel cardPanel;

	private String id;

	private int current = 0;

	public DefaultBeanWizardDialog(IBeanExtensionDialogView<E>... beanExtensionDialogs) {
		super();

		if (beanExtensionDialogs == null || beanExtensionDialogs.length == 0) {
			throw new IllegalArgumentException("beanExtensionDialogs must is not null");
		}

		this.beanExtensionDialogs = new ArrayList<IBeanExtensionDialogView<E>>(Arrays.asList(beanExtensionDialogs));

		this.validatorMap = new HashMap<IBeanExtensionDialogView<E>, ValidationResult>();

		for (IBeanExtensionDialogView<E> extensionDialogView : this.beanExtensionDialogs) {
			extensionDialogView.setBeanDialog(this);
		}

		initComponents();

		this.addContent(buildContents());
	}

	private void initComponents() {
		finishAction = new FinishAction();
		cancelAction = new CancelAction();
		previousAction = new PreviousAction();
		nextAction = new NextAction();

		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);

		beanExtensionDialogListModel = new DefaultListModel();
		pathBar = new JPathBar(beanExtensionDialogListModel);
		pathBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		pathBar.setFixedItemWidth(-1);
		pathBar.setFixedItemHeight(50);
		pathBar.setCellRenderer(new MyPathBarCellRenderer<E>());
	}

	private JComponent buildContents() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", //$NON-NLS-1$
				"FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(pathBar, cc.xy(1, 1));
		builder.add(cardPanel, cc.xy(1, 3));
		return builder.getPanel();
	}

	@Override
	public int showWizardDialog(IView parent, E bean, Map<String, Object> valueMap) {
		this.returnValue = CANCEL_OPTION;
		this.bean = bean;
		this.valueMap = valueMap;

		cardPanel.removeAll();
		beanExtensionDialogListModel.clear();

		StringBuilder sb = new StringBuilder("Wizard_");
		if (parent != null) {
			sb.append(parent.getClass().getName());
			sb.append("_");
		}
		for (IBeanExtensionDialogView<E> e : beanExtensionDialogs) {
			sb.append(e.getClass().getName());
			sb.append("_");
		}
		this.id = sb.toString();

		this.current = 0;

		MyValidatorListener vl = new MyValidatorListener();
		for (int i = 0; i < beanExtensionDialogs.size(); i++) {
			IBeanExtensionDialogView<E> b = beanExtensionDialogs.get(i);
			b.addValidatorListener(vl);

			cardPanel.add(getComponent(b), String.valueOf(i));

			beanExtensionDialogListModel.addElement(new BeanExtensionDialogViewState<E>(current == i ? StateEnum.PROGRESS : StateEnum.NOT_DONE, b, i + 1));
		}

		List<Action> actionTabList = buildActionTab();
		Action[] actionTab = actionTabList.toArray(new Action[actionTabList.size()]);
		if (getOthersActions().length != 0) {
			List<Action> actionList = new ArrayList<Action>();
			for (Action a : getOthersActions()) {
				actionList.add(a);
				a.setEnabled(false);
			}
			actionList.add(previousAction);
			actionList.add(nextAction);
			actionList.add(finishAction);
			actionList.add(cancelAction);
			actionTab = actionList.toArray(new Action[actionList.size()]);
		}

		dialog = new JDialogModel(getComponent(parent), beanExtensionDialogs.get(0).getTitle(), null, this, actionTab, new OpenActionListener(), cancelAction);

		previousAction.setEnabled(false);
		nextAction.setEnabled(false);
		finishAction.setEnabled(false);

		JButton nextButton = dialog.getButton(nextAction);
		nextButton.setHorizontalTextPosition(JButton.LEFT);

		dialog.setId(id);
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

	/**
	 * Build the action tab
	 * 
	 * @return
	 */
	protected List<Action> buildActionTab() {
		return CollectionHelper.asListOf(Action.class, previousAction, nextAction, finishAction, cancelAction);
	}

	protected Component getComponent(IView parent) {
		if (parent instanceof Component) {
			return (Component) parent;
		}
		return GUIWindow.getActiveWindow();
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

	protected Action[] getOthersActions() {
		return new Action[] {};
	}

	private void updateValidator() {
		IBeanExtensionDialogView<E> b = beanExtensionDialogs.get(current);
		ValidationResult r = validatorMap.get(b);

		boolean error = r.hasErrors();

		previousAction.setEnabled(current > 0);
		nextAction.setEnabled(current < beanExtensionDialogs.size() - 1 && !error);
		finishAction.setEnabled(current == beanExtensionDialogs.size() - 1 && !error);
	}

	@Override
	public void closeDialog() {
		for (IBeanExtensionDialogView<E> b : beanExtensionDialogs) {
			b.closeDialog();
		}

		dialog.closeDialog();
	}

	@Override
	public void accept() {
		if (finishAction.isEnabled()) {
			finishAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
		}
	}

	@Override
	public void previous() {
		if (previousAction.isEnabled()) {
			previousAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
		}
	}

	@Override
	public void next() {
		if (nextAction.isEnabled()) {
			nextAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
		}
	}

	@SuppressWarnings("unchecked")
	private void updateView() {
		IBeanExtensionDialogView<E> b = beanExtensionDialogs.get(current);
		b.setBean(bean, valueMap, false, false);

		cardPanel.removeAll();
		cardPanel.add(getComponent(b), "toto"); //$NON-NLS-1$
		cardPanel.revalidate();
		cardPanel.repaint();
		cardLayout.show(cardPanel, "toto");

		dialog.setTitle(b.getTitle());

		for (int i = 0; i < beanExtensionDialogListModel.getSize(); i++) {
			BeanExtensionDialogViewState<E> s = (BeanExtensionDialogViewState<E>) beanExtensionDialogListModel.getElementAt(i);
			s.state = current == i ? StateEnum.PROGRESS : (current > i ? StateEnum.DONE : StateEnum.NOT_DONE);
			beanExtensionDialogListModel.set(i, s);
		}

		b.openDialog();
	}

	private final class OpenActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			final JButton button = dialog.getButton(cancelAction);
			if (button != null) {
				button.requestFocus();
			}

			updateView();
		}
	}

	private final class FinishAction extends AbstractAcceptAction {

		private static final long serialVersionUID = -8520108575061780844L;

		public FinishAction() {
			super(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().finish());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			returnValue = ACCEPT_OPTION;

			IBeanExtensionDialogView<E> b = beanExtensionDialogs.get(current);
			b.commit(bean, valueMap);

			closeDialog();
		}
	}

	private final class CancelAction extends AbstractCancelAction {

		private static final long serialVersionUID = 3844373143848992876L;

		public CancelAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			returnValue = CANCEL_OPTION;
			closeDialog();
		}
	}

	private final class PreviousAction extends AbstractPreviousAction {

		private static final long serialVersionUID = 3083786158756879778L;

		public PreviousAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (current > 0) {
				IBeanExtensionDialogView<E> b = beanExtensionDialogs.get(current);
				b.commit(bean, valueMap);

				current--;
				updateView();
			}
		}
	}

	private final class NextAction extends AbstractNextAction {

		private static final long serialVersionUID = 3083786158756879778L;

		public NextAction() {
			super();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (current < beanExtensionDialogs.size() - 1) {
				IBeanExtensionDialogView<E> b = beanExtensionDialogs.get(current);
				b.commit(bean, valueMap);

				current++;
				updateView();
			}
		}
	}

	private final class MyValidatorListener implements BeanValidatorListener<E> {

		@Override
		public void updateValidator(IBeanExtensionDialogView<E> source, ValidationResult result) {
			validatorMap.put(source, result);

			DefaultBeanWizardDialog.this.updateValidator();
		}
	}

	private enum StateEnum {
		NOT_DONE, PROGRESS, DONE;
	}

	private static class BeanExtensionDialogViewState<E> {

		StateEnum state;

		IBeanExtensionDialogView<E> beanExtensionDialogView;

		int step;

		public BeanExtensionDialogViewState(StateEnum state, IBeanExtensionDialogView<E> beanExtensionDialogView, int step) {
			super();
			this.state = state;
			this.beanExtensionDialogView = beanExtensionDialogView;
			this.step = step;
		}

	}

	private static class MyPathBarCellRenderer<E> extends JPanel implements PathBarCellRenderer {

		private static final long serialVersionUID = -2063246129762054962L;

		private static final Color selectedColor = Color.GRAY;

		private static final Color normalColor = new Color(240, 240, 240);

		private JLabel nameLabel;

		private JLabel descLabel;

		private Font nameFont;

		private Font name2Font;

		private Font descFont;

		private Font desc2Font;

		public MyPathBarCellRenderer() {
			super();

			this.setLayout(new MyLayout());
			this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

			this.nameLabel = new JLabel();
			this.nameLabel.setHorizontalAlignment(JLabel.CENTER);
			this.add(nameLabel);

			this.descLabel = new JLabel();
			this.descLabel.setHorizontalAlignment(JLabel.CENTER);
			this.add(descLabel);
		}

		@SuppressWarnings("unchecked")
		@Override
		public Component getPathBarCellRendererComponent(JPathBar pathBar, Object value, int index, boolean selected) {
			if (nameFont == null) {
				nameFont = nameLabel.getFont().deriveFont(Font.BOLD);
				name2Font = nameLabel.getFont().deriveFont(Font.BOLD, nameLabel.getFont().getSize() + 3);
			}
			if (descFont == null) {
				descFont = descLabel.getFont().deriveFont(Font.ITALIC, 10);
				desc2Font = nameLabel.getFont().deriveFont(Font.ITALIC, 10);
			}

			BeanExtensionDialogViewState<E> step = (BeanExtensionDialogViewState<E>) value;
			if (step != null) {
				IBeanExtensionDialogView<E> beanExtensionDialogView = step.beanExtensionDialogView;
				if (beanExtensionDialogView.getSubtitle() != null) {
					nameLabel.setText(beanExtensionDialogView.getTitle());
					descLabel.setText(beanExtensionDialogView.getSubtitle());
				} else {
					nameLabel.setText(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().step(step.step));
					descLabel.setText(beanExtensionDialogView.getTitle());
				}

				switch (step.state) {
				case DONE:
					this.setBackground(selectedColor);
					nameLabel.setForeground(Color.LIGHT_GRAY);
					descLabel.setForeground(Color.LIGHT_GRAY);
					nameLabel.setFont(nameFont);
					descLabel.setFont(descFont);
					break;
				case NOT_DONE:
					this.setBackground(normalColor);
					nameLabel.setForeground(Color.black);
					descLabel.setForeground(Color.black);
					nameLabel.setFont(nameFont);
					descLabel.setFont(descFont);
					break;
				case PROGRESS:
					this.setBackground(selectedColor);
					nameLabel.setForeground(Color.white);
					descLabel.setForeground(Color.white);
					nameLabel.setFont(name2Font);
					descLabel.setFont(desc2Font);
					break;
				}
			} else {
				nameLabel.setText(null);
				descLabel.setText(null);
			}

			return this;
		}

		private class MyLayout implements LayoutManager {

			@Override
			public void layoutContainer(Container parent) {
				int width = parent.getWidth();
				int height = parent.getHeight();
				Insets insets = parent.getInsets();
				int w = width - (insets.left + insets.right);
				int h = height - (insets.top + insets.bottom);
				nameLabel.setBounds(insets.left, insets.top, w, h / 2);
				descLabel.setBounds(insets.left, insets.top + h / 2, w, h / 2);
			}

			@Override
			public void addLayoutComponent(String name, Component comp) {
			}

			@Override
			public void removeLayoutComponent(Component comp) {
			}

			@Override
			public Dimension preferredLayoutSize(Container parent) {
				Insets insets = parent.getInsets();
				Dimension nd = nameLabel.getPreferredSize();
				Dimension dd = descLabel.getPreferredSize();
				return new Dimension(insets.left + insets.right + Math.max(nd.width, dd.width), insets.top + insets.bottom + Math.max(nd.height, dd.height));
			}

			@Override
			public Dimension minimumLayoutSize(Container parent) {
				return preferredLayoutSize(parent);
			}
		}
	}
}
