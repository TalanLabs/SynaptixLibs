package com.synaptix.taskmanager.view.swing.dialog;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.ValidationUtils;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.entity.IdRaw;
import com.synaptix.swing.JSyScrollPane;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.utils.ToolBarFactory;
import com.synaptix.swing.widget.JSyTextField;
import com.synaptix.taskmanager.antlr.GraphCalcHelper;
import com.synaptix.taskmanager.helper.TaskManagerHelper;
import com.synaptix.taskmanager.helper.TaskManagerViewHelper;
import com.synaptix.taskmanager.helper.TaskObjectClassViewHelper;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.model.TaskServiceDescriptorFields;
import com.synaptix.taskmanager.model.TaskTypeFields;
import com.synaptix.taskmanager.objecttype.IObjectType;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.controller.ITaskManagerController;
import com.synaptix.taskmanager.view.swing.graph.JTaskChainGraphComponent;
import com.synaptix.widget.actions.view.swing.AbstractAddAction;
import com.synaptix.widget.actions.view.swing.AbstractDeleteAction;
import com.synaptix.widget.view.swing.JTextAreaMaxLength;
import com.synaptix.widget.view.swing.ValidationListener;
import com.synaptix.widget.view.swing.dialog.AbstractBeanExtensionDialog;
import com.synaptix.widget.view.swing.tablemodel.DefaultComponentTableModel;

public class GeneralTaskChainBeanExtensionDialog extends AbstractBeanExtensionDialog<ITaskChain> {

	private static final long serialVersionUID = 4251806106513281014L;

	private static final String[] TASK_TYPE_TABLE_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(TaskTypeFields.code(), TaskTypeFields.serviceCode(), TaskTypeFields.nature(),
			TaskTypeFields.checkSkippable(), TaskTypeFields.executantRole(), TaskTypeFields.managerRole());

	private final ITaskManagerController taskManagerController;

	private JTextField codeTextField;

	private JComboBox objectTypeComboBox;

	private JTextArea descriptionTextArea;

	private JTextField graphRuleTextField;

	private Action addTaskTypeAction;

	private Action deleteTaskTypeAction;

	private DefaultComponentTableModel<ITaskType> taskTypeDefaultComponentTableModel;

	private JSyTable taskTypeTable;

	private ValidationListener validationListener;

	private Map<String, Object> taskTypeFilters = new HashMap<String, Object>();

	private JTaskChainGraphComponent graphComponent;

	public GeneralTaskChainBeanExtensionDialog(ITaskManagerController taskManagerController) {
		super(StaticCommonHelper.getCommonConstantsBundle().general());

		this.taskManagerController = taskManagerController;

		initialize();
	}

	@Override
	protected void initComponents() {
		addTaskTypeAction = new AddTaskTypeAction();
		addTaskTypeAction.setEnabled(false);
		deleteTaskTypeAction = new DeleteTaskTypeAction();
		deleteTaskTypeAction.setEnabled(false);

		codeTextField = new JSyTextField(240);
		objectTypeComboBox = TaskObjectClassViewHelper.createTaskObjectClassComboBox(true);
		descriptionTextArea = new JTextAreaMaxLength(240);
		descriptionTextArea.setLineWrap(true);
		descriptionTextArea.setWrapStyleWord(true);
		graphRuleTextField = new JSyTextField(2000);

		taskTypeDefaultComponentTableModel = new DefaultComponentTableModel<ITaskType>(ITaskType.class, StaticHelper.getTaskTypeTableConstantsBundle(), TASK_TYPE_TABLE_COLUMNS);
		taskTypeTable = new JSyTable(taskTypeDefaultComponentTableModel, GeneralTaskChainBeanExtensionDialog.class.getName() + "-TaskType");

		taskTypeTable.getYColumnModel().getColumn(taskTypeDefaultComponentTableModel.findColumnIndexById(TaskTypeFields.nature().name()))
				.setCellRenderer(TaskManagerViewHelper.createEnumServiceNatureTableCellRenderer());
		taskTypeTable.getYColumnModel().getColumn(taskTypeDefaultComponentTableModel.findColumnIndexById(TaskTypeFields.managerRole().name()))
				.setCellRenderer(new MyManagerRoleSubstanceDefaultTableCellRenderer());
		taskTypeTable.getYColumnModel().getColumn(taskTypeDefaultComponentTableModel.findColumnIndexById(TaskTypeFields.executantRole().name()))
				.setCellRenderer(new MyExecutantRoleSubstanceDefaultTableCellRenderer());
		taskTypeTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					deleteTaskTypeAction.setEnabled(!readOnly && taskTypeTable.getSelectedRowCount() > 0);
				}
			}
		});
		taskTypeTable.registerKeyboardAction(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (taskTypeTable.getSelectedRowCount() == 1) {
					ITaskType taskType = taskTypeDefaultComponentTableModel.getComponent(taskTypeTable.convertRowIndexToModel(taskTypeTable.getSelectedRow()));
					StringSelection ss = new StringSelection(taskType.getCode());
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
				}
			}
		}, "Copy", KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false), JComponent.WHEN_FOCUSED);

		objectTypeComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				taskTypeFilters.put(TaskServiceDescriptorFields.objectType().name(), objectTypeComboBox.getSelectedItem());
			}
		});

		graphComponent = new JTaskChainGraphComponent();

		graphRuleTextField.getDocument().addDocumentListener(new GraphRuleDocumentListener());

		validationListener = new ValidationListener(this);
		codeTextField.getDocument().addDocumentListener(validationListener);
		objectTypeComboBox.addActionListener(validationListener);
		graphRuleTextField.getDocument().addDocumentListener(validationListener);
	}

	@Override
	protected JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("RIGHT:MAX(40DLU;PREF):NONE,FILL:4DLU:NONE,FILL:150DLU:NONE,FILL:DEFAULT:GROW(1.0)");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.append(StaticHelper.getTaskChainTableConstantsBundle().code(), codeTextField);
		builder.append(StaticHelper.getTaskChainTableConstantsBundle().objectType(), objectTypeComboBox);
		builder.append(ToolBarFactory.buildToolBar(addTaskTypeAction, null, deleteTaskTypeAction), builder.getColumnCount());
		builder.appendRow(builder.getLineGapSpec());
		builder.nextLine(2);
		builder.appendRow(builder.getDefaultRowSpec());
		builder.appendRow("FILL:80DLU:NONE");
		builder.add(new JSyScrollPane(taskTypeTable), new CellConstraints(1, builder.getRow(), builder.getColumnCount(), 2));
		builder.nextLine(2);
		builder.append(StaticHelper.getTaskChainTableConstantsBundle().graphRule(), graphRuleTextField, 2);
		builder.appendRow(builder.getLineGapSpec());
		builder.nextLine(2);
		builder.appendRow(builder.getDefaultRowSpec());
		builder.appendRow("FILL:100DLU:GROW(1.0)");
		builder.add(graphComponent, new CellConstraints(1, builder.getRow(), builder.getColumnCount(), 2));
		builder.nextLine(2);

		builder.append(StaticHelper.getTaskChainTableConstantsBundle().description());
		builder.appendRow("FILL:30DLU:NONE");
		builder.add(new JScrollPane(descriptionTextArea), new CellConstraints(3, builder.getRow(), 1, 2));
		builder.nextLine(2);

		return builder.getPanel();
	}

	@Override
	public void openDialog() {
		validationListener.setEnabled(false);

		codeTextField.setText(bean.getCode());
		objectTypeComboBox.setSelectedItem(bean.getObjectType());
		descriptionTextArea.setText(bean.getDescription());
		graphRuleTextField.setText(bean.getGraphRule() != null ? GraphCalcHelper.replaceId(bean.getGraphRule(), new GraphCalcHelper.IReplaceId() {
			@Override
			public String getOtherId(String id) {
				ITaskType taskType = ComponentHelper.findComponentBy(bean.getTaskTypes(), TaskTypeFields.id().name(), new IdRaw(id));
				return taskType != null ? taskType.getCode() : id;
			}
		}) : null);
		taskTypeDefaultComponentTableModel.setComponents(bean.getTaskTypes());

		validationListener.setEnabled(true);

		codeTextField.setEditable(!readOnly);
		objectTypeComboBox.setEnabled(!readOnly);
		descriptionTextArea.setEnabled(!readOnly);
		graphRuleTextField.setEditable(!readOnly);

		addTaskTypeAction.setEnabled(!readOnly);

		updateValidation();
		updateGraph();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void commit(ITaskChain bean, Map<String, Object> valueMap) {
		bean.setCode(codeTextField.getText().trim());
		bean.setObjectType((Class<? extends ITaskObject<?>>) objectTypeComboBox.getSelectedItem());
		bean.setDescription(descriptionTextArea.getText().trim());
		String graphRule = graphRuleTextField.getText().trim();
		bean.setGraphRuleReadable(graphRule);
		bean.setGraphRule(graphRule != null ? GraphCalcHelper.replaceId(graphRule, new GraphCalcHelper.IReplaceId() {
			@Override
			public String getOtherId(String id) {
				ITaskType taskType = ComponentHelper.findComponentBy(taskTypeDefaultComponentTableModel.getComponentList(), TaskTypeFields.code().name(), id);
				return taskType != null ? ((IdRaw) taskType.getId()).getHex() : id;
			}
		}) : null);
		bean.setTaskTypes(taskTypeDefaultComponentTableModel.getComponentList());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void updateValidation(ValidationResult result) {
		if (ValidationUtils.isBlank(codeTextField.getText())) {
			result.add(new SimpleValidationMessage(StaticCommonHelper.getCommonConstantsBundle().mandatoryField(), Severity.ERROR, codeTextField));
		} else if (!GraphCalcHelper.isValidId(codeTextField.getText())) {
			result.add(new SimpleValidationMessage(StaticHelper.getTaskChainTableConstantsBundle().codeMustHaveCharacters(), Severity.ERROR, codeTextField));
		}

		if (objectTypeComboBox.getSelectedItem() == null) {
			result.add(new SimpleValidationMessage(StaticCommonHelper.getCommonConstantsBundle().mandatoryField(), Severity.ERROR, objectTypeComboBox));
		}

		List<ITaskType> taskTypes = taskTypeDefaultComponentTableModel.getComponentList();
		if (taskTypes == null) {
			result.add(new SimpleValidationMessage(StaticCommonHelper.getCommonConstantsBundle().mandatoryField(), Severity.ERROR, taskTypeDefaultComponentTableModel));
		}

		Class<? extends ITaskObject<?>> objectType = (Class<? extends ITaskObject<?>>) objectTypeComboBox.getSelectedItem();
		if (taskTypes != null) {
			for (ITaskType tt : taskTypes) {
				if (!tt.getObjectType().equals(objectType)) {
					result.add(new SimpleValidationMessage(StaticHelper.getTaskChainTableConstantsBundle().taskTypeMustHaveSameObjectType(), Severity.ERROR, taskTypeDefaultComponentTableModel));
				}
			}
		}

		if (ValidationUtils.isBlank(graphRuleTextField.getText())) {
			result.add(new SimpleValidationMessage(StaticCommonHelper.getCommonConstantsBundle().mandatoryField(), Severity.ERROR, graphRuleTextField));
		} else if (!GraphCalcHelper.isValidGraphRule(graphRuleTextField.getText())) {
			result.add(new SimpleValidationMessage(StaticHelper.getTaskChainTableConstantsBundle().graphRuleIsNotValidLanguage(), Severity.ERROR, graphRuleTextField));
		} else {
			List<String> ids = GraphCalcHelper.extractId(graphRuleTextField.getText());
			if (ids != null && !ids.isEmpty()) {
				boolean doublon = false;
				Iterator<String> it = ids.iterator();
				while (it.hasNext() && !doublon) {
					String id = it.next();
					if (Collections.frequency(ids, id) > 1) {
						doublon = true;
					}

					if (taskTypes != null) {
						boolean find = false;
						Iterator<ITaskType> it2 = taskTypes.iterator();
						while (it2.hasNext() && !find) {
							ITaskType taskType = it2.next();
							find = taskType.getCode().equals(id);
						}

						if (!find) {
							result.add(new SimpleValidationMessage(StaticHelper.getTaskChainTableConstantsBundle().codeMustBeTaskType(), Severity.ERROR, graphRuleTextField));
						}
					}
				}
				if (doublon) {
					result.add(new SimpleValidationMessage(StaticHelper.getTaskChainTableConstantsBundle().codeMustBeUnique(), Severity.ERROR, graphRuleTextField));
				}
			}
		}
	}

	private void updateGraph() {
		graphComponent.setGraphRule(graphRuleTextField.getText(), taskTypeDefaultComponentTableModel.getComponentList());
	}

	private final class AddTaskTypeAction extends AbstractAddAction {

		private static final long serialVersionUID = -7340117570424711750L;

		@Override
		public void actionPerformed(ActionEvent e) {
			taskManagerController.getTaskTypesSearchDialogContext(GeneralTaskChainBeanExtensionDialog.this, taskTypeFilters).searchList(new IResultCallback<List<ITaskType>>() {
				@Override
				public void setResult(List<ITaskType> e) {
					if (e != null) {
						List<ITaskType> res = taskTypeDefaultComponentTableModel.getComponentList();
						if (res == null) {
							res = new ArrayList<ITaskType>();
						}
						for (ITaskType tt : e) {
							if (!res.contains(tt)) {
								res.add(tt);
							}
						}
						taskTypeDefaultComponentTableModel.setComponents(res);

						updateValidation();
					}
				}
			});
		}
	}

	private final class DeleteTaskTypeAction extends AbstractDeleteAction {

		private static final long serialVersionUID = 1269332096754128863L;

		@Override
		public void actionPerformed(ActionEvent e) {
			List<ITaskType> tts = new ArrayList<ITaskType>();
			for (int i : taskTypeTable.getSelectedRows()) {
				tts.add(taskTypeDefaultComponentTableModel.getComponent(taskTypeTable.convertRowIndexToModel(i)));
			}

			List<ITaskType> res = taskTypeDefaultComponentTableModel.getComponentList();
			for (ITaskType tt : tts) {
				res.remove(tt);
			}
			taskTypeDefaultComponentTableModel.setComponents(res);

			updateValidation();
		}
	}

	private final class GraphRuleDocumentListener implements DocumentListener {

		@Override
		public void removeUpdate(DocumentEvent e) {
			updateGraph();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			updateGraph();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			updateGraph();
		}
	}

	private final class MyManagerRoleSubstanceDefaultTableCellRenderer extends SubstanceDefaultTableCellRenderer {

		private static final long serialVersionUID = 3236782632017314391L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component res = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			ITaskType taskType = taskTypeDefaultComponentTableModel.getComponent(table.convertRowIndexToModel(row));
			if (taskType != null) {
				if (value != null && taskType.getObjectType() != null) {
					IObjectType<?, ?, ?, ?> objectType = TaskManagerHelper.getObjectTypeManager().getObjectType2(taskType.getObjectType());
					setText(objectType, (String) value);
				} else {
					setText(StaticCommonHelper.getCommonConstantsBundle().none());
				}
			}

			return res;
		}

		private <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> void setText(
				IObjectType<E, F, ManagerRole, ExecutantRole> objectType, String value) {
			if (objectType != null) {
				setText(objectType.getManagerRoleMeaning(Enum.valueOf(objectType.getManagerRoleClass(), value)));
			} else {
				setText(value);
			}
		}
	}

	private final class MyExecutantRoleSubstanceDefaultTableCellRenderer extends SubstanceDefaultTableCellRenderer {

		private static final long serialVersionUID = 3236782632017314391L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component res = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			ITaskType taskType = taskTypeDefaultComponentTableModel.getComponent(table.convertRowIndexToModel(row));
			if (taskType != null) {
				if (value != null && taskType.getObjectType() != null) {
					IObjectType<?, ?, ?, ?> objectType = TaskManagerHelper.getObjectTypeManager().getObjectType2(taskType.getObjectType());
					setText(objectType, (String) value);
				} else {
					setText(StaticCommonHelper.getCommonConstantsBundle().none());
				}
			}

			return res;
		}

		private <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> void setText(
				IObjectType<E, F, ManagerRole, ExecutantRole> objectType, String value) {
			if (objectType != null) {
				setText(objectType.getExecutantRoleMeaning(Enum.valueOf(objectType.getExecutantRoleClass(), value)));
			} else {
				setText(value);
			}
		}
	}
}
