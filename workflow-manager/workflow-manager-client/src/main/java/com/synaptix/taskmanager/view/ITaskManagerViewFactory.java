package com.synaptix.taskmanager.view;

import com.synaptix.taskmanager.controller.AbstractTodosManagementController;
import com.synaptix.taskmanager.controller.ErrorsManagementController;
import com.synaptix.taskmanager.controller.ITaskManagerController;
import com.synaptix.taskmanager.controller.SimulationTasksGraphController;
import com.synaptix.taskmanager.controller.StatusGraphsManagementController;
import com.synaptix.taskmanager.controller.TaskChainsManagementController;
import com.synaptix.taskmanager.controller.TaskServiceDescriptorsManagementController;
import com.synaptix.taskmanager.controller.TaskTypesManagementController;
import com.synaptix.taskmanager.controller.TasksGraphController;
import com.synaptix.taskmanager.controller.TasksManagementController;
import com.synaptix.taskmanager.controller.TodoFoldersManagementController;
import com.synaptix.taskmanager.controller.dialog.edit.TaskTypeDialogController;
import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.ITaskServiceDescriptor;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.model.ITodoFolder;
import com.synaptix.widget.component.view.IComponentsManagementViewDescriptor;
import com.synaptix.widget.component.view.ISearchComponentsDialogViewDescriptor;
import com.synaptix.widget.crud.view.descriptor.ICRUDManagementViewDescriptor;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;

public interface ITaskManagerViewFactory extends ICommonViewFactory {

	/**
	 * Create task service descriptor dialog view descriptor
	 */
	ISearchComponentsDialogViewDescriptor<ITaskServiceDescriptor> createSearchTaskServiceDescriptorDialogViewDescriptor();

	/**
	 * Create task service descriptor view descriptor
	 */
	IComponentsManagementViewDescriptor<ITaskServiceDescriptor> createTaskServiceDescriptorViewDescriptor(TaskServiceDescriptorsManagementController taskServiceDescriptorsManagementController);

	/**
	 * Create task type crud management view descriptor
	 */
	ICRUDManagementViewDescriptor<ITaskType> createTaskTypeViewDescriptor(TaskTypesManagementController taskTypesManagementController);

	/**
	 * Create a general Task Type bean extension dialog view
	 */
	IBeanExtensionDialogView<ITaskType> newGeneralTaskTypeBeanExtensionDialogView(TaskTypeDialogController taskTypeDialogController,
			ITaskManagerController taskManagerController);

	/**
	 * Create task chain crud management view descriptor
	 */
	com.synaptix.taskmanager.view.descriptor.ITaskChainsManagementViewDescriptor createTaskChainViewDescriptor(TaskChainsManagementController taskChainsManagementController);

	/**
	 * Create task type dialog view descriptor
	 */
	ISearchComponentsDialogViewDescriptor<ITaskType> createSearchTaskTypeDialogViewDescriptor();

	/**
	 * Create a general task chain bean extension dialog view
	 */
	IBeanExtensionDialogView<ITaskChain> newGeneralTaskChainBeanExtensionDialogView(ITaskManagerController taskManagerController);

	/**
	 * Create a general status graph bean extension dialog view
	 */
	IBeanExtensionDialogView<IStatusGraph> newGeneralStatusGraphBeanExtensionDialogView(ITaskManagerController taskManagerController,
			Class<? extends ITaskObject<?>> taskObjectClass);

	/**
	 * Create task chain dialog view descriptor
	 */
	ISearchComponentsDialogViewDescriptor<ITaskChain> createSearchTaskChainDialogViewDescriptor();

	/**
	 * Create task view descriptor
	 */
	com.synaptix.taskmanager.view.descriptor.ITasksManagementViewDescriptor createTaskViewDescriptor(TasksManagementController tasksManagementController);

	/**
	 * Create a status graphs management view
	 */
	com.synaptix.taskmanager.view.IStatusGraphsManagementView newStatusGraphsManagementView(StatusGraphsManagementController statusGraphsManagementController);

	/**
	 * Create a task graph view
	 */
	ITasksGraphView newTaskGraphView(TasksGraphController taskGraphController);

	com.synaptix.taskmanager.view.descriptor.IErrorsManagementViewDescriptor createErrorsViewDescriptor(ErrorsManagementController errorsManagementController);

	/**
	 * Create view descriptor for todo folders management.
	 * 
	 * @param todoFoldersManagementController
	 *            Controller
	 * @return View
	 */
	ICRUDManagementViewDescriptor<ITodoFolder> createTodoFolderViewDescriptor(TodoFoldersManagementController todoFoldersManagementController);

	/**
	 * Creates dialog for todo folder creation and edition
	 */
	IBeanExtensionDialogView<ITodoFolder> newGeneralTodoFolderExtensionBeanDialogView();

	/**
	 * Creates dialog for todo folder search
	 */
	ISearchComponentsDialogViewDescriptor<ITodoFolder> createSearchTodoFolderDialogViewDescriptor();

	/**
	 * Create custom panel for todo management.
	 */
	ITodoManagementView createTodoPanel(AbstractTodosManagementController abstractTodosManagementController);

	/**
	 * Create a task graph view
	 */
	ITasksGraphSimulationView newTaskGraphSimulationView(SimulationTasksGraphController simulationTasksGraphController);

}
