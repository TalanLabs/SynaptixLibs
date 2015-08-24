package com.synaptix.taskmanager.view.swing.dialog;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultListCellRenderer;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.taskmanager.helper.TaskManagerHelper;
import com.synaptix.taskmanager.helper.TaskManagerViewHelper;
import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.model.TaskTypeFields;
import com.synaptix.taskmanager.model.domains.ServiceNature;
import com.synaptix.taskmanager.objecttype.IObjectType;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.controller.ITaskManagerController;
import com.synaptix.widget.searchfield.view.swing.DefaultSearchFieldWidget;
import com.synaptix.widget.view.swing.ValidationListener;
import com.synaptix.widget.view.swing.dialog.AbstractBeanExtensionDialog;
import com.synaptix.widget.view.swing.helper.EnumViewHelper;


public class GeneralStatusGraphBeanExtensionDialog extends AbstractBeanExtensionDialog<IStatusGraph> {

	private static final long serialVersionUID = 4251806106513281014L;

	private final ITaskManagerController taskManagerController;

	private final Class<? extends ITaskObject<?>> taskObjectClass;

	private DefaultSearchFieldWidget<ITaskType> taskTypeDefaultSearchFieldWidget;

	private JComboBox currentStatusComboBox;

	private JComboBox nextStatusComboBox;

	private ValidationListener validationListener;

	public GeneralStatusGraphBeanExtensionDialog(ITaskManagerController taskManagerController, Class<? extends ITaskObject<?>> taskObjectClass) {
		super(StaticCommonHelper.getCommonConstantsBundle().general());

		this.taskManagerController = taskManagerController;
		this.taskObjectClass = taskObjectClass;

		initialize();
	}

	@Override
	protected void initComponents() {
		Map<String, Object> filters = new HashMap<String, Object>();
		filters.put(TaskTypeFields.objectType().name(), taskObjectClass);
		filters.put(TaskTypeFields.nature().name(), ServiceNature.UPDATE_STATUS);
		taskTypeDefaultSearchFieldWidget = TaskManagerViewHelper.createTaskTypeDefaultSearchFieldWidget(this, filters);

		currentStatusComboBox = new JComboBox();
		currentStatusComboBox.setRenderer(new MyStatusSubstanceDefaultListCellRenderer());
		nextStatusComboBox = new JComboBox();
		nextStatusComboBox.setRenderer(new MyStatusSubstanceDefaultListCellRenderer());

		taskTypeDefaultSearchFieldWidget.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// updateStatusComboBox();
			}
		});

		validationListener = new ValidationListener(this);
		taskTypeDefaultSearchFieldWidget.addChangeListener(validationListener);
		currentStatusComboBox.addActionListener(validationListener);
		nextStatusComboBox.addActionListener(validationListener);
	}

	@Override
	protected JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("RIGHT:MAX(40DLU;PREF):NONE,FILL:4DLU:NONE,FILL:150DLU:NONE,FILL:DEFAULT:GROW(1.0)", //$NON-NLS-1$
				""); //$NON-NLS-1$
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.append(StaticHelper.getStatusGraphTableConstantsBundle().currentStatus(), currentStatusComboBox);
		builder.append(StaticHelper.getStatusGraphTableConstantsBundle().nextStatus(), nextStatusComboBox);
		builder.append(StaticHelper.getStatusGraphTableConstantsBundle().taskType_code(), taskTypeDefaultSearchFieldWidget);
		return builder.getPanel();
	}

	@Override
	public void openDialog() {
		validationListener.setEnabled(false);

		taskTypeDefaultSearchFieldWidget.setValue(bean.getTaskType());

		IObjectType<?, ?, ?, ?> objectType = TaskManagerHelper.getObjectTypeManager().getObjectType2(taskObjectClass);
		currentStatusComboBox.setModel(createEnumStatusComboBoxModel(objectType));
		nextStatusComboBox.setModel(createEnumStatusComboBoxModel(objectType));
		currentStatusComboBox.setSelectedItem(getEnum(objectType, bean.getCurrentStatus()));
		nextStatusComboBox.setSelectedItem(getEnum(objectType, bean.getNextStatus()));

		validationListener.setEnabled(true);

		taskTypeDefaultSearchFieldWidget.setEnabled(!readOnly);
		currentStatusComboBox.setEnabled(!readOnly);
		nextStatusComboBox.setEnabled(!readOnly);


		updateValidation();
	}

	private <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> E getEnum(
			IObjectType<E, F, ManagerRole, ExecutantRole> objectType, String value) {
		if (objectType != null && value != null) {
			return Enum.valueOf(objectType.getStatusClass(), value);
		}
		return null;
	}

	@Override
	public void commit(IStatusGraph bean, Map<String, Object> valueMap) {
		ITaskType taskType = taskTypeDefaultSearchFieldWidget.getValue();
		bean.setObjectType(taskType != null ? taskType.getObjectType() : null);
		bean.setIdTaskType(taskType != null ? taskType.getId() : null);
		bean.setTaskType(taskType);
		bean.setCurrentStatus(currentStatusComboBox.getSelectedItem() != null ? ((Enum<?>) currentStatusComboBox.getSelectedItem()).name() : null);
		bean.setNextStatus(nextStatusComboBox.getSelectedItem() != null ? ((Enum<?>) nextStatusComboBox.getSelectedItem()).name() : null);
	}

	@Override
	protected void updateValidation(ValidationResult result) {
		if (taskTypeDefaultSearchFieldWidget.getValue() == null) {
			result.add(new SimpleValidationMessage(StaticCommonHelper.getCommonConstantsBundle().mandatoryField(), Severity.ERROR, taskTypeDefaultSearchFieldWidget));
		}

		if (nextStatusComboBox.getSelectedItem() == null) {
			result.add(new SimpleValidationMessage(StaticCommonHelper.getCommonConstantsBundle().mandatoryField(), Severity.ERROR, nextStatusComboBox));
		}
	}

	private <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> ComboBoxModel createEnumStatusComboBoxModel(
			IObjectType<E, F, ManagerRole, ExecutantRole> objectType) {
		return EnumViewHelper.createEnumComboBoxModel(objectType.getStatusClass().getEnumConstants(), true);
	}

	private final class MyStatusSubstanceDefaultListCellRenderer extends SubstanceDefaultListCellRenderer {

		private static final long serialVersionUID = 3236782632017314391L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component res = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			// ITaskType taskType = taskTypeDefaultSearchFieldWidget.getValue();
			if (value != null && taskObjectClass != null) {
				IObjectType<?, ?, ?, ?> objectType = TaskManagerHelper.getObjectTypeManager().getObjectType2(taskObjectClass);
				setText(objectType, ((Enum<?>) value).name());
			} else {
				setText(StaticCommonHelper.getCommonConstantsBundle().none());
			}

			return res;
		}

		private <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> void setText(
				IObjectType<E, F, ManagerRole, ExecutantRole> objectType, String value) {
			if (objectType != null) {
				setText(Enum.valueOf(objectType.getStatusClass(), value) + " - " + objectType.getStatusMeaning(Enum.valueOf(objectType.getStatusClass(), value)));
			} else {
				setText(value);
			}
		}
	}

}
