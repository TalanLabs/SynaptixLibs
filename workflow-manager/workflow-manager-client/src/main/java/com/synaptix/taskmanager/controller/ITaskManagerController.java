package com.synaptix.taskmanager.controller;

import java.util.List;
import java.util.Map;

import com.synaptix.client.view.IView;
import com.synaptix.entity.IId;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.ITaskServiceDescriptor;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.model.ITodoFolder;
import com.synaptix.widget.component.controller.context.ISearchDialogContext;
import com.synaptix.widget.searchfield.context.ISearchFieldWidgetContext;

public interface ITaskManagerController {

	/**
	 * Search field widget context for task service descriptor
	 */
	ISearchFieldWidgetContext<ITaskServiceDescriptor> getTaskServiceDescriptorSearchFieldWidgetContext(IView parent, Map<String, Object> filters);

	/**
	 * Search dialog context for task type
	 */
	ISearchDialogContext<ITaskType> getTaskTypesSearchDialogContext(IView parent, Map<String, Object> filters);

	/**
	 * Search field widget context for task type
	 */
	ISearchFieldWidgetContext<ITaskType> getTaskTypeSearchFieldWidgetContext(IView parent, Map<String, Object> filters);

	/**
	 * Search dialog context for task chain
	 */
	ISearchDialogContext<ITaskChain> getTaskChainsSearchDialogContext(IView parent, Map<String, Object> filters);

	/**
	 * Show task management for task object
	 */
	<E extends ITaskObject<?>> void searchTasksBy(E taskObject);

	/**
	 * Show errors management for a task object.
	 */
	<E extends ITaskObject<?>> void searchErrorsByObject(E taskObject);

	/**
	 * Show errors for a task.
	 *
	 * @param idTask
	 *            ID of the task.
	 */
	void searchErrorsByTask(IId idTask);

	ISearchDialogContext<ITodoFolder> getTodoFolderSearchDialogContext(IView parent, Map<String, Object> filters);

	ISearchFieldWidgetContext<ITodoFolder> getTodoFolderSearchFieldWidgetContext(IView parent, Map<String, Object> filters);

	<E extends ITaskObject<?>> void loadTasksGraphByObject(E taskObject);

	/**
	 * Show simulation for object type
	 */
	void showTasksGraphSimulation(Class<? extends ITaskObject<?>> objectType);

	/**
	 * Reload todos screen (keeps todo folder selection if possible)
	 */
	void refreshTodoList(List<IId> idObjects);

	void reloadTodoDetails();

	void refreshTodoList(IId idObject);

	ISearchFieldWidgetContext<ITaskChain> getTaskChainsearchFieldWidgetContext(IView parent, Map<String, Object> filters);

	void loadTodoFoldersList();
}
