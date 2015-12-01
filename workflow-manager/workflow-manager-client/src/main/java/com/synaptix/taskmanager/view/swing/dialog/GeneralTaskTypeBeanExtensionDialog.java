package com.synaptix.taskmanager.view.swing.dialog;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.joda.time.Duration;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultListCellRenderer;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.ValidationUtils;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.swing.utils.SwingComponentFactory;
import com.synaptix.swing.widget.JSyTextField;
import com.synaptix.taskmanager.antlr.GraphCalcHelper;
import com.synaptix.taskmanager.controller.ITaskManagerController;
import com.synaptix.taskmanager.controller.dialog.edit.TaskTypeDialogController;
import com.synaptix.taskmanager.helper.TaskManagerHelper;
import com.synaptix.taskmanager.helper.TaskManagerViewHelper;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.ITaskServiceDescriptor;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.model.ITodoFolder;
import com.synaptix.taskmanager.objecttype.IObjectType;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.widget.helper.FormValidationHelper;
import com.synaptix.widget.searchfield.view.swing.DefaultSearchFieldWidget;
import com.synaptix.widget.view.swing.JTextAreaMaxLength;
import com.synaptix.widget.view.swing.ValidationListener;
import com.synaptix.widget.view.swing.dialog.AbstractBeanExtensionDialog;
import com.synaptix.widget.view.swing.helper.EnumViewHelper;

public class GeneralTaskTypeBeanExtensionDialog extends AbstractBeanExtensionDialog<ITaskType> {

	private static final long serialVersionUID = 4251806106513281014L;

	private final TaskTypeDialogController taskTypeDialogController;

	private final ITaskManagerController taskManagerController;

	private JTextField codeTextField;

	private JTextField meaningTextField;

	private JComboBox managerRoleComboBox;

	private JComboBox executantRoleComboBox;

	private JCheckBox checkSkippableCheckBox;

	private DefaultSearchFieldWidget<ITaskServiceDescriptor> taskServiceDescriptorDefaultSearchFieldWidget;

	private JTextArea descriptionTextArea;

	private JFormattedTextField todoManagerDurationFormattedTextField;

	private DefaultSearchFieldWidget<ITodoFolder> todoFolderSearchFieldWidget;

	private JFormattedTextField resultDepthTextField;

	private ValidationListener validationListener;

	public GeneralTaskTypeBeanExtensionDialog(TaskTypeDialogController taskTypeDialogController, ITaskManagerController taskManagerController) {
		super(StaticCommonHelper.getCommonConstantsBundle().general());

		this.taskTypeDialogController = taskTypeDialogController;
		this.taskManagerController = taskManagerController;

		initialize();
	}

	@Override
	protected void initComponents() {
		codeTextField = new JSyTextField(240);
		meaningTextField = new JSyTextField(70);
		managerRoleComboBox = new JComboBox();
		managerRoleComboBox.setRenderer(new MyManagerRoleSubstanceDefaultListCellRenderer());
		executantRoleComboBox = new JComboBox();
		executantRoleComboBox.setRenderer(new MyExecutantRoleSubstanceDefaultListCellRenderer());
		checkSkippableCheckBox = new JCheckBox();
		taskServiceDescriptorDefaultSearchFieldWidget = TaskManagerViewHelper.createTaskServiceDescriptorDefaultSearchFieldWidget(this, new HashMap<String, Object>());
		descriptionTextArea = new JTextAreaMaxLength(240);
		descriptionTextArea.setLineWrap(true);
		descriptionTextArea.setWrapStyleWord(true);
		todoManagerDurationFormattedTextField = SwingComponentFactory.createLongField();
		todoFolderSearchFieldWidget = TaskManagerViewHelper.createTodoFolderDefaultSearchFieldWidget(this, new HashMap<String, Object>());
		resultDepthTextField = SwingComponentFactory.createIntegerField();

		taskServiceDescriptorDefaultSearchFieldWidget.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				ITaskServiceDescriptor taskServiceDescriptor = taskServiceDescriptorDefaultSearchFieldWidget.getValue();
				if (taskServiceDescriptor != null) {
					codeTextField.setText(taskServiceDescriptor.getCode());
					descriptionTextArea.setText(taskServiceDescriptor.getDescription());
				}
				updateRoleComboBox();
			}
		});

		validationListener = new ValidationListener(this);
		codeTextField.getDocument().addDocumentListener(validationListener);
		taskServiceDescriptorDefaultSearchFieldWidget.addChangeListener(validationListener);
		resultDepthTextField.getDocument().addDocumentListener(validationListener);
	}

	@Override
	protected JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("RIGHT:MAX(40DLU;PREF):NONE,FILL:4DLU:NONE,FILL:150DLU:NONE", //$NON-NLS-1$
				""); //$NON-NLS-1$
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.append(StaticHelper.getTaskTypeTableConstantsBundle().serviceCode(), taskServiceDescriptorDefaultSearchFieldWidget);
		builder.append(StaticHelper.getTaskTypeTableConstantsBundle().code(), codeTextField);
		builder.append(StaticHelper.getTaskTypeTableConstantsBundle().meaning(), meaningTextField);
		builder.append(StaticHelper.getTaskTypeTableConstantsBundle().checkSkippable(), checkSkippableCheckBox);
		builder.append(StaticHelper.getTaskTypeTableConstantsBundle().managerRole(), managerRoleComboBox);
		builder.append(StaticHelper.getTaskTypeTableConstantsBundle().executantRole(), executantRoleComboBox);
		builder.append(StaticHelper.getTaskTypeTableConstantsBundle().todoManagerDuration(), todoManagerDurationFormattedTextField);
		builder.append(StaticHelper.getTaskManagerConstantsBundle().todoFolder(), todoFolderSearchFieldWidget);
		builder.append(StaticHelper.getTaskTypeTableConstantsBundle().resultDepth(), resultDepthTextField);
		builder.append(StaticHelper.getTaskTypeTableConstantsBundle().description());
		builder.appendRow("FILL:30DLU:NONE");
		builder.add(new JScrollPane(descriptionTextArea), new CellConstraints(3, builder.getRow(), 1, 2));
		builder.nextLine(2);
		return builder.getPanel();
	}

	@Override
	public void openDialog() {
		taskTypeDialogController.loadTaskServiceDescriptor(bean.getServiceCode(), new IResultCallback<ITaskServiceDescriptor>() {
			@Override
			public void setResult(ITaskServiceDescriptor taskServiceDescriptor) {
				validationListener.setEnabled(false);

				codeTextField.setText(bean.getCode());
				meaningTextField.setText(bean.getMeaning());
				taskServiceDescriptorDefaultSearchFieldWidget.setValue(taskServiceDescriptor);
				updateRoleComboBox();

				if (taskServiceDescriptor != null && taskServiceDescriptor.getObjectType() != null) {
					IObjectType<?, ?, ?, ?> objectType = TaskManagerHelper.getObjectTypeManager().getObjectType2(taskServiceDescriptor.getObjectType());
					managerRoleComboBox.setSelectedItem(getEnumManagerRole(objectType, bean.getManagerRole()));
					executantRoleComboBox.setSelectedItem(getEnumExecutantRole(objectType, bean.getExecutantRole()));
				} else {
					managerRoleComboBox.setSelectedItem(null);
					executantRoleComboBox.setSelectedItem(null);
				}

				checkSkippableCheckBox.setSelected(bean.isCheckSkippable());
				descriptionTextArea.setText(bean.getDescription());

				todoManagerDurationFormattedTextField.setValue(bean.getTodoManagerDuration() != null ? bean.getTodoManagerDuration().getStandardHours() : null);

				todoFolderSearchFieldWidget.setValue(bean.getTodoFolder() != null ? bean.getTodoFolder() : null);
				resultDepthTextField.setValue(bean.getResultDepth());

				validationListener.setEnabled(true);

				codeTextField.setEditable(!readOnly);
				meaningTextField.setEditable(!readOnly);
				managerRoleComboBox.setEnabled(!readOnly);
				executantRoleComboBox.setEnabled(!readOnly);
				checkSkippableCheckBox.setEnabled(!readOnly);
				taskServiceDescriptorDefaultSearchFieldWidget.setEnabled(!readOnly);
				descriptionTextArea.setEnabled(!readOnly);
				todoFolderSearchFieldWidget.setEnabled(!readOnly);
				resultDepthTextField.setEnabled(!readOnly);

				updateValidation();

			}
		});
	}

	private <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> ManagerRole getEnumManagerRole(
			IObjectType<E, F, ManagerRole, ExecutantRole> objectType, String value) {
		if (objectType != null && value != null) {
			return Enum.valueOf(objectType.getManagerRoleClass(), value);
		}
		return null;
	}

	private <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> ExecutantRole getEnumExecutantRole(
			IObjectType<E, F, ManagerRole, ExecutantRole> objectType, String value) {
		if (objectType != null && value != null) {
			return Enum.valueOf(objectType.getExecutantRoleClass(), value);
		}
		return null;
	}

	@Override
	public void commit(ITaskType bean, Map<String, Object> valueMap) {
		ITaskServiceDescriptor taskServiceDescriptor = taskServiceDescriptorDefaultSearchFieldWidget.getValue();
		bean.setServiceCode(taskServiceDescriptor != null ? taskServiceDescriptor.getCode() : null);
		bean.setObjectType(taskServiceDescriptor != null ? taskServiceDescriptor.getObjectType() : null);
		bean.setNature(taskServiceDescriptor != null ? taskServiceDescriptor.getNature() : null);

		bean.setCode(codeTextField.getText().trim());
		bean.setMeaning(meaningTextField.getText().trim());
		bean.setManagerRole(managerRoleComboBox.getSelectedItem() != null ? ((Enum<?>) managerRoleComboBox.getSelectedItem()).name() : null);
		bean.setExecutantRole(executantRoleComboBox.getSelectedItem() != null ? ((Enum<?>) executantRoleComboBox.getSelectedItem()).name() : null);
		bean.setCheckSkippable(checkSkippableCheckBox.isSelected());
		bean.setDescription(descriptionTextArea.getText().trim());
		bean.setTodoManagerDuration(todoManagerDurationFormattedTextField.getValue() != null ? new Duration(((Long) todoManagerDurationFormattedTextField.getValue()).longValue() * 60L * 60L * 1000L)
				: null);
		bean.setResultDepth((Integer) resultDepthTextField.getValue());

		ITodoFolder todoFolder = todoFolderSearchFieldWidget.getValue();
		bean.setIdTodoFolder(todoFolder != null ? todoFolder.getId() : null);
	}

	@Override
	protected void updateValidation(ValidationResult result) {
		if (ValidationUtils.isBlank(codeTextField.getText())) {
			result.add(new SimpleValidationMessage(StaticCommonHelper.getCommonConstantsBundle().mandatoryField(), Severity.ERROR, codeTextField));
		} else if (!GraphCalcHelper.isValidId(codeTextField.getText())) {
			result.add(new SimpleValidationMessage(StaticHelper.getTaskTypeTableConstantsBundle().codeMustHaveCharacters(), Severity.ERROR, codeTextField));
		}

		if (taskServiceDescriptorDefaultSearchFieldWidget.getValue() == null) {
			result.add(new SimpleValidationMessage(StaticCommonHelper.getCommonConstantsBundle().mandatoryField(), Severity.ERROR, taskServiceDescriptorDefaultSearchFieldWidget));
		}
		if (!FormValidationHelper.validateFormattedTextField(resultDepthTextField, "^-?[0-9]+$")) {
			result.add(new SimpleValidationMessage(StaticCommonHelper.getCommonConstantsBundle().mandatoryField(), Severity.ERROR, resultDepthTextField));
		} else {
			int v = (Integer) resultDepthTextField.getValue();
			if ((v < -1) || (v > 9)) {
				result.add(new SimpleValidationMessage(StaticHelper.getTaskTypeTableConstantsBundle().resultDepthNumber(), Severity.ERROR, resultDepthTextField));
			}
		}
	}

	private void updateRoleComboBox() {
		ITaskServiceDescriptor taskServiceDescriptor = taskServiceDescriptorDefaultSearchFieldWidget.getValue();
		ComboBoxModel managerRoleModel;
		ComboBoxModel executantRoleModel;
		if (taskServiceDescriptor != null && taskServiceDescriptor.getObjectType() != null) {
			IObjectType<?, ?, ?, ?> objectType = TaskManagerHelper.getObjectTypeManager().getObjectType2(taskServiceDescriptor.getObjectType());
			managerRoleModel = createEnumManagerRoleComboBoxModel(objectType);
			executantRoleModel = createEnumExecutantRoleComboBoxModel(objectType);
		} else {
			managerRoleModel = new DefaultComboBoxModel();
			executantRoleModel = new DefaultComboBoxModel();
		}
		managerRoleComboBox.setModel(managerRoleModel);
		executantRoleComboBox.setModel(executantRoleModel);
	}

	private <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> ComboBoxModel createEnumManagerRoleComboBoxModel(
			IObjectType<E, F, ManagerRole, ExecutantRole> objectType) {
		return EnumViewHelper.createEnumComboBoxModel(objectType.getManagerRoleClass().getEnumConstants(), true);
	}

	private <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> ComboBoxModel createEnumExecutantRoleComboBoxModel(
			IObjectType<E, F, ManagerRole, ExecutantRole> objectType) {
		return EnumViewHelper.createEnumComboBoxModel(objectType.getExecutantRoleClass().getEnumConstants(), true);
	}

	private final class MyManagerRoleSubstanceDefaultListCellRenderer extends SubstanceDefaultListCellRenderer {

		private static final long serialVersionUID = 3236782632017314391L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component res = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			ITaskServiceDescriptor taskServiceDescriptor = taskServiceDescriptorDefaultSearchFieldWidget.getValue();
			if (taskServiceDescriptor != null) {
				if (value != null && taskServiceDescriptor.getObjectType() != null) {
					IObjectType<?, ?, ?, ?> objectType = TaskManagerHelper.getObjectTypeManager().getObjectType2(taskServiceDescriptor.getObjectType());
					setText(objectType, ((Enum<?>) value).name());
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

	private final class MyExecutantRoleSubstanceDefaultListCellRenderer extends SubstanceDefaultListCellRenderer {

		private static final long serialVersionUID = 3236782632017314391L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component res = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			ITaskServiceDescriptor taskServiceDescriptor = taskServiceDescriptorDefaultSearchFieldWidget.getValue();
			if (taskServiceDescriptor != null) {
				if (value != null && taskServiceDescriptor.getObjectType() != null) {
					IObjectType<?, ?, ?, ?> objectType = TaskManagerHelper.getObjectTypeManager().getObjectType2(taskServiceDescriptor.getObjectType());
					setText(objectType, ((Enum<?>) value).name());
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
