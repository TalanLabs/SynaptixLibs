package com.synaptix.widget.hierarchical.column;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.synaptix.client.view.IView;
import com.synaptix.component.IComponent;
import com.synaptix.component.field.IField;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.swing.IconFeedbackComponentValidationResultPanel;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.JListToList;
import com.synaptix.swing.SwingMessages;
import com.synaptix.swing.tooltip.ToolTipFeedbackComponentValidationResultFocusListener;
import com.synaptix.swing.utils.GUIWindow;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.widget.actions.view.swing.AbstractAcceptAction;
import com.synaptix.widget.actions.view.swing.AbstractCancelAction;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.JSyHierarchicalPanel;
import com.synaptix.widget.hierarchical.view.swing.component.helper.HierarchicalDataHelper;
import com.synaptix.widget.hierarchical.view.swing.component.title.IHierarchicalTitleColumnElement;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceListCellRenderer;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.IValidationView;
import com.synaptix.widget.view.swing.ValidationListener;

public class HierarchicalVisibilityColumnsDialog<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends JPanel implements IValidationView {

	private static final long serialVersionUID = -5213595834965784882L;

	private static final String TEXT_TITLE = SwingMessages.getString("DialogVisibilityColumns.0"); //$NON-NLS-1$

	private final JSyHierarchicalPanel<E, F, L> syHierarchicalPanel;

	private final ConfigurationContext<E, F, L> configurationContext;

	private final ValidationResultModel validationResultModel;

	private JComponent keyTypedResultView;

	private Action acceptAction;

	private Action cancelAction;

	private JDialogModel dialog;

	// private List<JCheckBox> visibleBoxs;

	private MyListToList listToList;

	private ValidationListener validationListener;

	protected ToolTipFeedbackComponentValidationResultFocusListener toolTipFeedbackComponentValidationResultFocusListener;

	public HierarchicalVisibilityColumnsDialog(JSyHierarchicalPanel<E, F, L> syHierarchicalPanel, ConfigurationContext<E, F, L> configurationContext) {
		super(new BorderLayout());

		this.syHierarchicalPanel = syHierarchicalPanel;
		this.configurationContext = configurationContext;

		this.validationResultModel = new DefaultValidationResultModel();

		this.keyTypedResultView = ValidationResultViewFactory.createReportIconAndTextLabel(validationResultModel);
		if(hasValidationOnFocus()) {
			toolTipFeedbackComponentValidationResultFocusListener = new ToolTipFeedbackComponentValidationResultFocusListener(validationResultModel);
		}

		initActions();
		initComponents();

		this.add(buildContent(), BorderLayout.CENTER);
	}

	private void initActions() {
		acceptAction = new AcceptAction();
		cancelAction = new CancelAction();
	}

	private void initComponents() {
		validationListener = new ValidationListener(this);

		listToList = new MyListToList();
		ListCellRenderer cellRenderer = new TypeGenericSubstanceListCellRenderer<IField>(new GenericObjectToString<IField>() {

			@Override
			public String getString(IField t) {
				final IHierarchicalTitleColumnElement hierarchicalTitleColumnElement = syHierarchicalPanel.getTitleColumnDefinition(t);
				String name = configurationContext.getTranslationBundle().getString(t.name());
				if (hierarchicalTitleColumnElement.isLock()) {
					return StaticWidgetHelper.getSynaptixWidgetConstantsBundle().columnLocked(name);
				}
				return name;
			}
		});
		listToList.getAllList().setCellRenderer(cellRenderer);
		listToList.getSelectionList().setCellRenderer(cellRenderer);
		listToList.addChangeListener(validationListener);
		// visibleBoxs = new ArrayList<JCheckBox>();

		initLists();
	}

	private final void initLists() {
		List<IField> columnList = new ArrayList<IField>();
		List<IField> visibleColumnList = new ArrayList<IField>();
		for (IHierarchicalTitleColumnElement hierarchicalTitleColumnElement : syHierarchicalPanel.getTitleColumnDefinitions()) {
			IField field = hierarchicalTitleColumnElement.getColumnElement();
			// if (!hierarchicalTitleColumnElement.isLock()) {
			columnList.add(field);

			if (hierarchicalTitleColumnElement.isColumnVisible()) {
				visibleColumnList.add(field);
			}
			// }

			// JCheckBox checkBoxColumn = new JCheckBox(configurationContext.getTranslationBundle().getString(field.name()));
			// checkBoxColumn.putClientProperty("field", hierarchicalTitleColumnElement.getColumnElement());
			// checkBoxColumn.setSelected(hierarchicalTitleColumnElement.isColumnVisible());
			// if (hierarchicalTitleColumnElement.isLock()) {
			// checkBoxColumn.setEnabled(false);
			// } else {
			// checkBoxColumn.setEnabled(true);
			// }

			// checkBoxColumn.addActionListener(validationListener);
			//
			// visibleBoxs.add(checkBoxColumn);
		}
		listToList.setAllValues(columnList.toArray(new IField[columnList.size()]));
		listToList.setSelectionValues(visibleColumnList.toArray(new IField[visibleColumnList.size()]));

		// updateEnableCheckBox();
	}

	@Override
	public void updateValidation() {
		ValidationResult result = new ValidationResult();
		boolean hasOneUnchecked = false;
		if ((listToList.getSelectionValues() != null) && (listToList.getSelectionValues().length > 0)) {
			hasOneUnchecked = true;
		}
		// Iterator<JCheckBox> ite = visibleBoxs.iterator();
		// while ((ite.hasNext()) && (!hasOneUnchecked)) {
		// JCheckBox checkBox = ite.next();
		// if (checkBox.isSelected()) {
		// hasOneUnchecked = true;
		// }
		// }
		if (!hasOneUnchecked) {
			result.add(new SimpleValidationMessage(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().aColumnAtLeastMustBeSelected(), Severity.ERROR));
		}

		validationResultModel.setResult(result);
		acceptAction.setEnabled(!result.hasErrors());
	}

	private JComponent buildContent() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", //$NON-NLS-1$
				"FILL:PREF:GROW(1.0),CENTER:3DLU:NONE,FILL:MAX(22DLU;PREF):NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.add(new IconFeedbackComponentValidationResultPanel(validationResultModel, buildEditorPanel()), cc.xy(1, 1));
		builder.add(keyTypedResultView, cc.xy(1, 3));
		return builder.getPanel();
	}

	public JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:NONE");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.append(listToList);
		// for (JCheckBox checkBox : visibleBoxs) {
		// builder.append(checkBox);
		// }
		return builder.getPanel();
	}

	public void showDialog(IView view) {
		dialog = new JDialogModel(getComponent(view), TEXT_TITLE, this, new Action[] { acceptAction, cancelAction }, cancelAction);
		dialog.showDialog();
		dialog.dispose();
	}

	private Component getComponent(IView parent) {
		if (parent instanceof Component) {
			return (Component) parent;
		}
		return GUIWindow.getActiveWindow();
	}

	private final class AcceptAction extends AbstractAcceptAction {

		private static final long serialVersionUID = -888022090279482448L;

		@Override
		public void actionPerformed(ActionEvent e) {
			// syHierarchicalPanel.setTitleColumnIsAdjusting(true);
			// for (JCheckBox checkBox : visibleBoxs) {
			// syHierarchicalPanel.changeTitleColumnVisibility((IField) checkBox.getClientProperty("field"), checkBox.isSelected());
			// }
			List<IHierarchicalTitleColumnElement> list = new ArrayList<IHierarchicalTitleColumnElement>();
			ListModel allModel = listToList.getAllList().getModel();
			for (int i = 0; i < allModel.getSize(); i++) {
				Object obj = allModel.getElementAt(i);
				IField field = (IField) obj;
				final IHierarchicalTitleColumnElement hierarchicalTitleColumnElement = syHierarchicalPanel.getTitleColumnDefinition(field);
				list.add(HierarchicalDataHelper.buildTitleElement(field, false, hierarchicalTitleColumnElement.isLock()));
				// syHierarchicalPanel.changeTitleColumnVisibility(field, false);
			}
			for (Object obj : listToList.getSelectionValues()) {
				IField field = (IField) obj;
				final IHierarchicalTitleColumnElement hierarchicalTitleColumnElement = syHierarchicalPanel.getTitleColumnDefinition(field);
				list.add(HierarchicalDataHelper.buildTitleElement(field, true, hierarchicalTitleColumnElement.isLock()));
				// syHierarchicalPanel.changeTitleColumnVisibility(field, true);
			}
			syHierarchicalPanel.setTitleColumnDefinition(list);
			// syHierarchicalPanel.setTitleColumnIsAdjusting(false);
			dialog.closeDialog();
		}
	}

	private final class CancelAction extends AbstractCancelAction {

		private static final long serialVersionUID = -4052796104386578191L;

		@Override
		public void actionPerformed(ActionEvent e) {
			dialog.closeDialog();
		}
	}

	private final class MyListToList extends JListToList<IField> {

		private static final long serialVersionUID = -4658381980813473945L;

		@Override
		protected void removeObjects(Object[] os) {
			List<Object> removableObjects = new ArrayList<Object>();
			if (os != null) {
				for (Object obj : os) {
					IField field = (IField) obj;
					final IHierarchicalTitleColumnElement hierarchicalTitleColumnElement = syHierarchicalPanel.getTitleColumnDefinition(field);
					if (!hierarchicalTitleColumnElement.isLock()) {
						removableObjects.add(field);
					}
				}
			}
			super.removeObjects(removableObjects.toArray(new Object[removableObjects.size()]));
		}

		@Override
		protected void addObjects(Object[] os, int index) {
			List<Object> addableObjects = new ArrayList<Object>();
			if (os != null) {
				for (Object obj : os) {
					IField field = (IField) obj;
					final IHierarchicalTitleColumnElement hierarchicalTitleColumnElement = syHierarchicalPanel.getTitleColumnDefinition(field);
					if (!hierarchicalTitleColumnElement.isLock()) {
						addableObjects.add(field);
					}
				}
			}
			super.addObjects(addableObjects.toArray(new Object[addableObjects.size()]), index);
		}

		@Override
		protected Action buildAddAllAction() {
			return new AbstractAction(">>") { //$NON-NLS-1$

				private static final long serialVersionUID = 2116827089301767668L;

				@Override
				public void actionPerformed(ActionEvent e) {
					List<Object> objectsToAdd = new ArrayList<Object>();
					ListModel model = getAllList().getModel();
					for (int i = 0; i < model.getSize(); i++) {
						Object obj = model.getElementAt(i);
						IField field = (IField) obj;
						final IHierarchicalTitleColumnElement hierarchicalTitleColumnElement = syHierarchicalPanel.getTitleColumnDefinition(field);
						if (!hierarchicalTitleColumnElement.isLock()) {
							objectsToAdd.add(field);
						}
					}
					addObjects(objectsToAdd.toArray(new Object[objectsToAdd.size()]), -1);
					fireChangeListener();
				}
			};
		}

		@Override
		protected Action buildRemoveAllAction() {
			return new AbstractAction("<<") { //$NON-NLS-1$

				private static final long serialVersionUID = 3862814566801721518L;

				@Override
				public void actionPerformed(ActionEvent e) {
					List<Object> objectsToRemove = new ArrayList<Object>();
					ListModel model = getSelectionList().getModel();
					for (int i = 0; i < model.getSize(); i++) {
						Object obj = model.getElementAt(i);
						IField field = (IField) obj;
						final IHierarchicalTitleColumnElement hierarchicalTitleColumnElement = syHierarchicalPanel.getTitleColumnDefinition(field);
						if (!hierarchicalTitleColumnElement.isLock()) {
							objectsToRemove.add(field);
						}
					}
					removeObjects(objectsToRemove.toArray(new Object[objectsToRemove.size()]));
					fireChangeListener();
				}
			};
		}
	}

	public boolean hasValidationOnFocus() {
		return true;
	}
}
