package com.synaptix.taskmanager.helper;

import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.table.TableCellRenderer;

import com.google.inject.Inject;
import com.synaptix.client.view.IView;
import com.synaptix.swing.search.Filter;
import com.synaptix.swing.search.filter.DefaultSuperComboBoxFilter;
import com.synaptix.swing.table.ExcelColumnRenderer;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.model.ITaskServiceDescriptor;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.model.ITodoFolder;
import com.synaptix.taskmanager.model.domains.ServiceNature;
import com.synaptix.taskmanager.model.domains.TaskStatus;
import com.synaptix.taskmanager.model.domains.TodoOwner;
import com.synaptix.taskmanager.model.domains.TodoStatus;
import com.synaptix.taskmanager.controller.ITaskManagerController;
import com.synaptix.widget.searchfield.view.swing.DefaultSearchFieldFilter;
import com.synaptix.widget.searchfield.view.swing.DefaultSearchFieldWidget;
import com.synaptix.widget.view.swing.helper.EnumViewHelper;

public class TaskManagerViewHelper {

	@Inject
	private static ITaskManagerController taskManagerController;

	// ServiceNature

	public static final DefaultSuperComboBoxFilter<ServiceNature> createEnumServiceNatureDefaultSuperComboBoxFilter(String id, String name) {
		return EnumViewHelper.createEnumDefaultSuperComboBoxFilter(id, name, ServiceNature.values(), StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().serviceNatures(), true);
	}

	public static final TableCellRenderer createEnumServiceNatureTableCellRenderer() {
		return EnumViewHelper.createEnumTableCellRenderer(ServiceNature.class, StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().serviceNatures());
	}

	public static final JComboBox createEnumServiceNatureComboBox() {
		return EnumViewHelper.createEnumComboBox(ServiceNature.values(), StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().serviceNatures(), true);
	}

	// ITaskServiceDescriptor

	public static final DefaultSearchFieldFilter<ITaskServiceDescriptor> createTaskServiceDescriptorDefaultSearchFieldFilter(IView parent, String id, String name, Map<String, Object> filters) {
		return new DefaultSearchFieldFilter<ITaskServiceDescriptor>(id, name, taskManagerController.getTaskServiceDescriptorSearchFieldWidgetContext(parent, filters),
				TaskManagerHelper.createTaskServiceDescriptorGenericObjectToString(), true, StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().enterALabel());
	}

	public static final DefaultSearchFieldWidget<ITaskServiceDescriptor> createTaskServiceDescriptorDefaultSearchFieldWidget(IView parent, Map<String, Object> filters) {
		return new DefaultSearchFieldWidget<ITaskServiceDescriptor>(taskManagerController.getTaskServiceDescriptorSearchFieldWidgetContext(parent, filters),
				TaskManagerHelper.createTaskServiceDescriptorGenericObjectToString(), true, StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().enterALabel());
	}

	// ITaskType

	public static final DefaultSearchFieldFilter<ITaskType> createTaskTypeDefaultSearchFieldFilter(IView parent, String id, String name, Map<String, Object> filters) {
		return new DefaultSearchFieldFilter<ITaskType>(id, name, taskManagerController.getTaskTypeSearchFieldWidgetContext(parent, filters), TaskManagerHelper.createTaskTypeGenericObjectToString(),
				true, StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().enterALabel());
	}

	public static final DefaultSearchFieldWidget<ITaskType> createTaskTypeDefaultSearchFieldWidget(IView parent, Map<String, Object> filters) {
		return new DefaultSearchFieldWidget<ITaskType>(taskManagerController.getTaskTypeSearchFieldWidgetContext(parent, filters), TaskManagerHelper.createTaskTypeGenericObjectToString(), true,
				StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().enterALabel());
	}

	// ITodoFolder

	public static final DefaultSearchFieldFilter<ITodoFolder> createTodoFolderDefaultSearchFieldFilter(IView parent, String id, String name, Map<String, Object> filters) {
		return new DefaultSearchFieldFilter<ITodoFolder>(id, name, taskManagerController.getTodoFolderSearchFieldWidgetContext(parent, filters),
				TaskManagerHelper.createTodoFolderGenericObjectToString(), true, StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().enterALabel());
	}

	public static final DefaultSearchFieldWidget<ITodoFolder> createTodoFolderDefaultSearchFieldWidget(IView parent, Map<String, Object> filters) {
		return new DefaultSearchFieldWidget<ITodoFolder>(taskManagerController.getTodoFolderSearchFieldWidgetContext(parent, filters), TaskManagerHelper.createTodoFolderGenericObjectToString(), true,
				StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().enterALabel());
	}

	// TaskStatus

	public static final DefaultSuperComboBoxFilter<TaskStatus> createEnumTaskStatusDefaultSuperComboBoxFilter(String id, String name) {
		return EnumViewHelper.createEnumDefaultSuperComboBoxFilter(id, name, TaskStatus.values(), StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().taskStatuses(), true);
	}

	public static final TableCellRenderer createEnumTaskStatusTableCellRenderer() {
		return EnumViewHelper.createEnumTableCellRenderer(TaskStatus.class, StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().taskStatuses());
	}

	public static final JComboBox createEnumTaskStatusComboBox() {
		return EnumViewHelper.createEnumComboBox(TaskStatus.values(), StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().taskStatuses(), true);
	}

	// ToDo table cell renderers
	public static final TableCellRenderer createEnumTodoStatusTableCellRenderer() {
		return EnumViewHelper.createEnumTableCellRenderer(TaskStatus.class, StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().todoStatuses());
	}

	public static TableCellRenderer createTaskOwnerEnumTableCellRenderer() {
		return EnumViewHelper.createEnumTableCellRenderer(TodoOwner.class, StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().todoOwners());
	}

	public static Filter createEnumTodoStatusSuperComboBoxFilter(String id, String name) {
		return EnumViewHelper.createEnumDefaultSuperComboBoxFilter(id, name, TodoStatus.values(), StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().todoStatuses(), true);
	}

	// Task Chains

	public static final DefaultSearchFieldFilter<ITaskChain> createTaskChainDefaultSearchFieldFilter(IView parent, String id, String name, Map<String, Object> filters) {
		return new DefaultSearchFieldFilter<ITaskChain>(id, name, taskManagerController.getTaskChainsearchFieldWidgetContext(parent, filters),
				TaskManagerHelper.createTaskChainGenericObjectToString(), true, StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().enterTaskChain(), true);
	}

	public static final TableCellRenderer createEnumServiceResultStatusTableCellRenderer() {
		return EnumViewHelper.createEnumTableCellRenderer(String.class, StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().serviceResultStatuses());
	}

	public static final ExcelColumnRenderer createEnumServiceResultStatusExcelColumnRenderer() {
		return EnumViewHelper.createExcelColumnRenderer(StaticTaskManagerHelper.getTaskManagerModuleConstantsBundle().serviceResultStatuses());
	}
}
