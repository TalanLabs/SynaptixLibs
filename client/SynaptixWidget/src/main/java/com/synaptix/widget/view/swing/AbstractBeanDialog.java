package com.synaptix.widget.view.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.synaptix.swing.JArrowScrollPane;
import com.synaptix.swing.JDialogModel;
import com.synaptix.widget.actions.view.swing.AbstractAcceptAction;
import com.synaptix.widget.actions.view.swing.AbstractCancelAction;
import com.synaptix.widget.actions.view.swing.AbstractCloseAction;
import com.synaptix.widget.view.swing.extension.DefaultBeanExtensionDialogCellRenderer;
import com.synaptix.widget.view.swing.extension.IBeanExtensionDialog;
import com.synaptix.widget.view.swing.extension.ValidatorListener;

public abstract class AbstractBeanDialog<E> extends JPanel {

	private static final long serialVersionUID = -5968015818775564189L;

	public static final int ACCEPT_OPTION = 0;

	public static final int CANCEL_OPTION = 1;

	private JDialogModel dialog;

	private Action acceptAction;

	private Action cancelAction;

	private Action closeAction;

	private int returnValue;

	private ValidationResultModel validationResultModel;

	private DefaultListModel beanExtensionDialogListModel;

	private JList list;

	private JArrowScrollPane listScrollPane;

	private CardLayout cardLayout;

	private JPanel cardPanel;

	private JPanel allPanel;

	private E bean;

	private Map<String, Map<String, Object>> extensionValueMap;

	private List<IBeanExtensionDialog<E>> beanExtensionDialogList;

	private Map<String, ValidationResult> validatorMap;

	private String defaultTitle;

	private String addTitle;

	private String addSubTitle;

	private String modifyTitle;

	private String modifySubTitle;

	private boolean hideListIfAlone;

	private boolean useCardLayout;

	private BeanExtensionDialogCellRenderer<E> beanExtensionDialogCellRenderer = new DefaultBeanExtensionDialogCellRenderer<E>();

	public AbstractBeanDialog(String defaultTitle) {
		this(defaultTitle, defaultTitle, defaultTitle);
	}

	public AbstractBeanDialog(String defaultTitle, String addTitle, String modifyTitle) {
		this(defaultTitle, addTitle, null, modifyTitle, null);
	}

	public AbstractBeanDialog(String defaultTitle, String addTitle, String addSubTitle, String modifyTitle, String modifySubTitle) {
		this(defaultTitle, addTitle, addSubTitle, modifyTitle, modifySubTitle, false);
	}

	public AbstractBeanDialog(String defaultTitle, String addTitle, String addSubTitle, String modifyTitle, String modifySubTitle, boolean hideListIfAlone) {
		super(new BorderLayout());

		this.defaultTitle = defaultTitle;
		this.addTitle = addTitle;
		this.addSubTitle = addSubTitle;
		this.modifyTitle = modifyTitle;
		this.modifySubTitle = modifySubTitle;
		this.hideListIfAlone = hideListIfAlone;

		validatorMap = new HashMap<String, ValidationResult>();

		initActions();
		initComponents();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initActions() {
		acceptAction = new AcceptAction();
		cancelAction = new CancelAction();
		closeAction = new CloseAction();
	}

	private void createComponents() {
		cardLayout = new CardLayout();
		cardPanel = new JPanel(cardLayout);

		beanExtensionDialogListModel = new DefaultListModel();
		list = new JList(beanExtensionDialogListModel);
		list.setBorder(BorderFactory.createEmptyBorder());
		listScrollPane = new JArrowScrollPane(list);
		listScrollPane.setBorder(BorderFactory.createEmptyBorder());

		allPanel = new JPanel(new BorderLayout());

		validationResultModel = new DefaultValidationResultModel();
	}

	private void initComponents() {
		createComponents();

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new BeanExtensionDialogListCellRenderer());
		list.getSelectionModel().addListSelectionListener(new BeanExtensionDialogListSelectionListener());

		// listScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	}

	private JComponent buildContents() {
		return allPanel;
	}

	protected abstract E createNewBean();

	protected abstract boolean isNewBean(E bean);

	protected abstract List<IBeanExtensionDialog<E>> initializeExtension(E bean, Map<String, Map<String, Object>> extensionValueMap, boolean readOnly);

	protected Action[] getOthersActions() {
		return new Action[] {};
	}

	public void setHideListIfAlone(boolean hideListIfAlone) {
		this.hideListIfAlone = hideListIfAlone;
	}

	public boolean isHideListIfAlone() {
		return hideListIfAlone;
	}

	private JComponent buildAllPanel() {
		FormLayout layout = new FormLayout("FILL:75DLU:NONE,FILL:DEFAULT:GROW(1.0)", //$NON-NLS-1$
				"FILL:DEFAULT:GROW(1.0)"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(listScrollPane, cc.xy(1, 1));
		builder.add(cardPanel, cc.xy(2, 1));
		return builder.getPanel();
	}

	public int showDialog(Component parent) {
		return showDialog(parent, null, null, false);
	}

	public int showDialog(Component parent, E bean, Map<String, Map<String, Object>> extensionValueMap, boolean readOnly) {
		if (bean == null) {
			bean = createNewBean();
		}
		if (extensionValueMap == null) {
			extensionValueMap = new HashMap<String, Map<String, Object>>();
		}

		beanExtensionDialogList = new ArrayList<IBeanExtensionDialog<E>>();
		List<IBeanExtensionDialog<E>> exList = initializeExtension(bean, extensionValueMap, readOnly);
		if (exList != null) {
			beanExtensionDialogList.addAll(exList);
		}

		cardPanel.removeAll();
		allPanel.removeAll();

		useCardLayout = !(hideListIfAlone && beanExtensionDialogList.size() == 1);

		MyValidatorListener vl = new MyValidatorListener();
		for (IBeanExtensionDialog<E> b : beanExtensionDialogList) {
			b.addValidatorListener(vl);

			if (useCardLayout) {
				cardPanel.add(b.getView(), b.getId());
			}

			b.setReadOnly(readOnly);

			if (!extensionValueMap.containsKey(b.getId()) || extensionValueMap.get(b.getId()) == null) {
				extensionValueMap.put(b.getId(), new HashMap<String, Object>());
			}
		}

		if (useCardLayout) {
			allPanel.add(buildAllPanel(), BorderLayout.CENTER);
		} else {
			allPanel.add(beanExtensionDialogList.get(0).getView(), BorderLayout.CENTER);
		}

		this.bean = bean;
		this.extensionValueMap = extensionValueMap;

		if (!readOnly) {

			Action[] actionTab = new Action[] { acceptAction, cancelAction };
			if (getOthersActions().length != 0) {

				List<Action> actionList = new ArrayList<Action>();
				actionList.add(acceptAction);
				for (Action a : getOthersActions()) {
					actionList.add(a);
					a.setEnabled(false);
				}
				actionList.add(closeAction);
				actionTab = actionList.toArray(new Action[actionList.size()]);
			}

			if (isNewBean(bean)) {
				dialog = new JDialogModel(parent, addTitle, addSubTitle, this, actionTab, new OpenActionListener(), cancelAction);
			} else {
				dialog = new JDialogModel(parent, modifyTitle, modifySubTitle, this, actionTab, new OpenActionListener(), cancelAction);
			}
		} else {
			dialog = new JDialogModel(parent, defaultTitle, null, this, new Action[] { closeAction }, new OpenActionListener(), closeAction);
		}

		acceptAction.setEnabled(false);
		dialog.setResizable(true);

		dialog.showDialog();
		dialog.dispose();

		for (IBeanExtensionDialog<E> b : beanExtensionDialogList) {
			b.removeValidatorListener(vl);

			cardPanel.remove(b.getView());
		}

		return returnValue;
	}

	public E getBean() {
		return bean;
	}

	public Map<String, Map<String, Object>> getExtensionValueMap() {
		return extensionValueMap;
	}

	private void updateValidator() {
		ValidationResult result = new ValidationResult();
		for (ValidationResult r : validatorMap.values()) {
			result.addAllFrom(r);
		}
		validationResultModel.setResult(result);
		acceptAction.setEnabled(!result.hasErrors());
		for (Action a : getOthersActions()) {
			a.setEnabled(!result.hasErrors());
		}
	}

	private void closeDialog() {
		for (IBeanExtensionDialog<E> b : beanExtensionDialogList) {
			b.closeDialog();
		}

		dialog.closeDialog();
	}

	private IBeanExtensionDialog<E> findBeanExtensionDialogById(String id) {
		IBeanExtensionDialog<E> res = null;
		int i = 0;
		while (i < beanExtensionDialogList.size() && res == null) {
			IBeanExtensionDialog<E> beanExtensionDialog = beanExtensionDialogList.get(i);
			if (beanExtensionDialog.getId().equals(id)) {
				res = beanExtensionDialog;
			}
			i++;
		}
		return res;
	}

	public void acceptDialog() {
		if (acceptAction.isEnabled()) {
			acceptAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "accept")); //$NON-NLS-1$
		}
	}

	public void cancelDialog() {
		if (cancelAction.isEnabled()) {
			cancelAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "cancel")); //$NON-NLS-1$
		}
	}

	public ValidationResult getValidationResult(String id) {
		return validatorMap.get(id);
	}

	private final class OpenActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			beanExtensionDialogListModel.clear();
			for (IBeanExtensionDialog<E> ex : beanExtensionDialogList) {
				beanExtensionDialogListModel.addElement(ex);
			}

			for (IBeanExtensionDialog<E> b : beanExtensionDialogList) {
				b.load(bean, extensionValueMap.get(b.getId()));
			}

			list.setSelectedIndex(0);
		}
	}

	private final class AcceptAction extends AbstractAcceptAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			updateValidator();
			if (acceptAction.isEnabled()) {
				returnValue = ACCEPT_OPTION;

				for (IBeanExtensionDialog<E> b : beanExtensionDialogList) {
					b.commit(bean, extensionValueMap.get(b.getId()));
				}

				closeDialog();
			}
		}
	}

	private final class CancelAction extends AbstractCancelAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			returnValue = CANCEL_OPTION;
			closeDialog();
		}
	}

	private final class CloseAction extends AbstractCloseAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			returnValue = CANCEL_OPTION;
			closeDialog();
		}
	}

	public interface BeanExtensionDialogCellRenderer<T> {

		public Component getCellRendererComponent(AbstractBeanDialog<T> beanDialog, IBeanExtensionDialog<T> led, boolean isSelected);

	}

	private final class BeanExtensionDialogListCellRenderer implements ListCellRenderer {

		@Override
		@SuppressWarnings("unchecked")
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			IBeanExtensionDialog<E> led = (IBeanExtensionDialog<E>) value;
			Component c = beanExtensionDialogCellRenderer.getCellRendererComponent(AbstractBeanDialog.this, led, isSelected);
			c.setPreferredSize(new Dimension(list.getWidth(), list.getWidth()));
			return c;
		}
	}

	private final class BeanExtensionDialogListSelectionListener implements ListSelectionListener {

		@Override
		@SuppressWarnings("unchecked")
		public void valueChanged(ListSelectionEvent e) {
			if (useCardLayout && !e.getValueIsAdjusting()) {
				IBeanExtensionDialog<E> b = (IBeanExtensionDialog<E>) list.getSelectedValue();

				// TODO Grosse verrue pour corriger le bug de refresh sur les
				// tableaux
				cardPanel.removeAll();
				cardPanel.add(b.getView(), "toto"); //$NON-NLS-1$
				cardPanel.revalidate();
				cardPanel.repaint();
				cardLayout.show(cardPanel, "toto"); //$NON-NLS-1$

				// cardLayout.show(cardPanel, b.getId());
			}
		}
	}

	private final class MyValidatorListener implements ValidatorListener {

		@Override
		public void updateValidator(Object source, String id, ValidationResult result) {
			validatorMap.put(id, result);

			AbstractBeanDialog.this.updateValidator();

			IBeanExtensionDialog<E> ex = findBeanExtensionDialogById(id);
			int index = beanExtensionDialogListModel.indexOf(ex);
			beanExtensionDialogListModel.set(index, ex);
		}
	}
}
