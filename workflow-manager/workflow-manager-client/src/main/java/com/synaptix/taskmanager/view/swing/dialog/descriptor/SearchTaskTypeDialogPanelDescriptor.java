package com.synaptix.taskmanager.view.swing.dialog.descriptor;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;

import org.pushingpixels.substance.api.renderers.SubstanceDefaultTableCellRenderer;

import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.search.Filter;
import com.synaptix.taskmanager.helper.TaskManagerHelper;
import com.synaptix.taskmanager.helper.TaskManagerViewHelper;
import com.synaptix.taskmanager.helper.TaskObjectClassViewHelper;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.model.TaskTypeFields;
import com.synaptix.taskmanager.objecttype.IObjectType;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.widget.component.view.swing.descriptor.DefaultDialogDescriptor;

public class SearchTaskTypeDialogPanelDescriptor extends DefaultDialogDescriptor<ITaskType> {

	private static final String[] FILTER_COLUMNS = ComponentHelper.PropertyArrayBuilder
			.build(TaskTypeFields.code(), TaskTypeFields.objectType(), TaskTypeFields.serviceCode(), TaskTypeFields.nature());

	private static final String[] TABLE_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(TaskTypeFields.code(), TaskTypeFields.objectType(), TaskTypeFields.serviceCode(), TaskTypeFields.nature(),
			TaskTypeFields.checkSkippable(), TaskTypeFields.managerRole(), TaskTypeFields.executantRole(), TaskTypeFields.description());

	public SearchTaskTypeDialogPanelDescriptor() {
		super(StaticHelper.getTaskTypeTableConstantsBundle(), FILTER_COLUMNS, TABLE_COLUMNS);
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
