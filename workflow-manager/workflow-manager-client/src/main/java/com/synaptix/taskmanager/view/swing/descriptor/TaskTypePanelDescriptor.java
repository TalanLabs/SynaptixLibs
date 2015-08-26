package com.synaptix.taskmanager.view.swing.descriptor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.joda.time.Duration;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

import com.synaptix.client.common.swing.view.DefaultNlsCRUDPanelDescriptor;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.component.helper.ComponentHelper.PropertyArrayBuilder;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.search.Filter;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.taskmanager.controller.TaskTypesManagementController;
import com.synaptix.taskmanager.helper.StaticTaskManagerHelper;
import com.synaptix.taskmanager.helper.TaskManagerHelper;
import com.synaptix.taskmanager.helper.TaskManagerViewHelper;
import com.synaptix.taskmanager.helper.TaskObjectClassViewHelper;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.model.TaskTypeFields;
import com.synaptix.taskmanager.objecttype.IObjectType;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.widget.model.RibbonData;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceTableCellRenderer;
import com.synaptix.widget.view.swing.helper.IToolBarActionsBuilder;

public class TaskTypePanelDescriptor extends DefaultNlsCRUDPanelDescriptor<ITaskType> {

	private static final String[] FILTER_COLUMNS = PropertyArrayBuilder.build(TaskTypeFields.code(), TaskTypeFields.objectType(), TaskTypeFields.serviceCode(), TaskTypeFields.nature());

	private static final String[] TABLE_COLUMNS = PropertyArrayBuilder.build(TaskTypeFields.code(), TaskTypeFields.objectType(), TaskTypeFields.serviceCode(), TaskTypeFields.meaning(),
			TaskTypeFields.nature(), TaskTypeFields.checkSkippable(), TaskTypeFields.managerRole(), TaskTypeFields.executantRole(), TaskTypeFields.todoManagerDuration(), TaskTypeFields.description(),
			TaskTypeFields.resultDepth());

	private Action createTaskChainAction;

	private TaskTypesManagementController taskTypesManagementController;

	public TaskTypePanelDescriptor(TaskTypesManagementController taskTypesManagementController) {
		super(new RibbonData(StaticHelper.getTaskManagerConstantsBundle().taskTypesManagement(), StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().taskManager(), 2,
				StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().configuration(), 1, "taskManager"), StaticHelper.getTaskTypeTableConstantsBundle(), FILTER_COLUMNS, TABLE_COLUMNS);
		this.taskTypesManagementController = taskTypesManagementController;
		initAction();
	}

	private void initAction() {
		createTaskChainAction = new AbstractAction(StaticHelper.getTaskManagerConstantsBundle().newTaskChain()) {

			private static final long serialVersionUID = 6295679400956880690L;

			@Override
			public void actionPerformed(ActionEvent e) {
				taskTypesManagementController.createNewTaskChain(getSelectedComponent());
			}
		};
		createTaskChainAction.setEnabled(false);
	}

	@Override
	public void installToolBar(IToolBarActionsBuilder builder) {
		super.installToolBar(builder);

		builder.addSeparator();
		builder.addAction(createTaskChainAction);
	}

	@Override
	public List<Filter> getSpecificFilters() {
		List<Filter> specificFilters = new ArrayList<Filter>();
		specificFilters.add(TaskManagerViewHelper.createEnumServiceNatureDefaultSuperComboBoxFilter(TaskTypeFields.nature().name(), StaticHelper.getTaskTypeTableConstantsBundle().nature()));
		specificFilters.add(TaskObjectClassViewHelper.createTaskObjectClassDefaultSuperComboBoxFilter(TaskTypeFields.objectType().name(), StaticHelper.getTaskTypeTableConstantsBundle().objectType(),
				true));
		return specificFilters;
	}

	@Override
	public void installTable(JSyTable table) {
		super.installTable(table);

		getTableColumn(table, TaskTypeFields.nature().name()).setCellRenderer(TaskManagerViewHelper.createEnumServiceNatureTableCellRenderer());
		getTableColumn(table, TaskTypeFields.objectType().name()).setCellRenderer(TaskObjectClassViewHelper.createTaskObjectClassTableCellRenderer());
		getTableColumn(table, TaskTypeFields.managerRole().name()).setCellRenderer(new MyManagerRoleSubstanceDefaultTableCellRenderer());
		getTableColumn(table, TaskTypeFields.executantRole().name()).setCellRenderer(new MyExecutantRoleSubstanceDefaultTableCellRenderer());
		getTableColumn(table, TaskTypeFields.todoManagerDuration().name()).setCellRenderer(new MyTodoManagerDurationTableCellRenderer());

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (getDefaultSearchComponentsPanel().getTable().getSelectedRowCount() == 1) {
						createTaskChainAction.setEnabled(getCRUDManagementController().hasAuthWrite());
					} else {
						createTaskChainAction.setEnabled(false);
					}
				}
			}
		});
	}

	private final class MyTodoManagerDurationTableCellRenderer extends TypeGenericSubstanceTableCellRenderer<Duration> {

		private static final long serialVersionUID = -2171627954700057007L;

		public MyTodoManagerDurationTableCellRenderer() {
			super(new GenericObjectToString<Duration>() {
				@Override
				public String getString(Duration t) {
					return t != null ? String.valueOf(t.getStandardHours()) : null;
				}
			});

			this.setHorizontalAlignment(SwingConstants.RIGHT);
		}
	}

	private final class MyManagerRoleSubstanceDefaultTableCellRenderer extends SubstanceDefaultTableCellRenderer {

		private static final long serialVersionUID = 3236782632017314391L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component res = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			ITaskType taskType = getTablePageComponentsPanel().getComponentTableModel().getComponent(table.convertRowIndexToModel(row));
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

			ITaskType taskType = getTablePageComponentsPanel().getComponentTableModel().getComponent(table.convertRowIndexToModel(row));
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
