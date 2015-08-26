package com.synaptix.taskmanager.view.swing.descriptor;

import java.util.ArrayList;
import java.util.List;

import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.search.Filter;
import com.synaptix.taskmanager.controller.TaskServiceDescriptorsManagementController;
import com.synaptix.taskmanager.helper.StaticTaskManagerHelper;
import com.synaptix.taskmanager.helper.TaskManagerViewHelper;
import com.synaptix.taskmanager.helper.TaskObjectClassViewHelper;
import com.synaptix.taskmanager.model.ITaskServiceDescriptor;
import com.synaptix.taskmanager.model.TaskServiceDescriptorFields;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.widget.component.view.swing.descriptor.DefaultComponentsManagementPanelDescriptor;
import com.synaptix.widget.model.RibbonData;

public class TaskServiceDescriptorPanelDescriptor extends DefaultComponentsManagementPanelDescriptor<ITaskServiceDescriptor> {

	private static final String[] FILTER_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(TaskServiceDescriptorFields.code(), TaskServiceDescriptorFields.category(),
			TaskServiceDescriptorFields.nature(), TaskServiceDescriptorFields.objectType());

	private static final String[] TABLE_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(TaskServiceDescriptorFields.code(), TaskServiceDescriptorFields.objectType(),
			TaskServiceDescriptorFields.nature(), TaskServiceDescriptorFields.category(), TaskServiceDescriptorFields.description());

	public TaskServiceDescriptorPanelDescriptor(TaskServiceDescriptorsManagementController taskServicesDescriptorsManagementController) {
		super(new RibbonData(StaticHelper.getTaskManagerConstantsBundle().taskServiceDescriptorsManagement(), StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().taskManager(), 1,
				StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().configuration(), 1, "taskManager"), StaticHelper.getTaskServiceDescriptorTableConstantsBundle(), FILTER_COLUMNS, TABLE_COLUMNS);
	}

	@Override
	public List<Filter> getSpecificFilters() {
		List<Filter> specificFilters = new ArrayList<Filter>();
		specificFilters.add(TaskManagerViewHelper.createEnumServiceNatureDefaultSuperComboBoxFilter(TaskServiceDescriptorFields.nature().name(), StaticHelper
				.getTaskServiceDescriptorTableConstantsBundle().nature()));
		specificFilters.add(TaskObjectClassViewHelper.createTaskObjectClassDefaultSuperComboBoxFilter(TaskServiceDescriptorFields.objectType().name(), StaticHelper
				.getTaskServiceDescriptorTableConstantsBundle().objectType(), true));
		return specificFilters;
	}

	@Override
	public void installTable(JSyTable table) {
		super.installTable(table);

		getTableColumn(table, TaskServiceDescriptorFields.nature().name()).setCellRenderer(TaskManagerViewHelper.createEnumServiceNatureTableCellRenderer());
		getTableColumn(table, TaskServiceDescriptorFields.objectType().name()).setCellRenderer(TaskObjectClassViewHelper.createTaskObjectClassTableCellRenderer());
	}
}
