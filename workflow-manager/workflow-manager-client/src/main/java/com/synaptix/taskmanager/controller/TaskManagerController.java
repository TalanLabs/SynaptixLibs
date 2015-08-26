package com.synaptix.taskmanager.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.synaptix.client.service.IFrontendContext;
import com.synaptix.client.service.IFrontendContextInitialize;
import com.synaptix.client.view.IView;
import com.synaptix.taskmanager.controller.context.TaskChainSearchDialogContext;
import com.synaptix.taskmanager.controller.context.TaskChainSearchFieldWidgetContext;
import com.synaptix.taskmanager.controller.context.TaskServiceDescriptorSearchFieldWidgetContext;
import com.synaptix.taskmanager.controller.context.TaskTypeSearchDialogContext;
import com.synaptix.taskmanager.controller.context.TaskTypeSearchFieldWidgetContext;
import com.synaptix.taskmanager.controller.context.TodoFolderSearchDialogContext;
import com.synaptix.taskmanager.controller.context.TodoFolderSearchFieldWidgetContext;
import com.synaptix.taskmanager.helper.StaticTaskManagerHelper;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.ITaskServiceDescriptor;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.model.ITodoFolder;
import com.synaptix.taskmanager.urimanager.IURIClientManagerDiscovery;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.widget.component.controller.context.ISearchDialogContext;
import com.synaptix.widget.core.controller.AbstractController;
import com.synaptix.widget.searchfield.context.ISearchFieldWidgetContext;

public class TaskManagerController extends AbstractController implements ITaskManagerController, IFrontendContextInitialize {

	private final ITaskManagerViewFactory viewFactory;

	private TasksGraphController tasksGraphController;

	private TasksManagementController tasksManagementController;

	private ErrorsManagementController errorsManagementController;

	private SimulationTasksGraphController simulationTasksGraphController;

	private TaskChainsManagementController taskChainsManagementController;

	@Inject
	private Provider<TaskChainsManagementController> taskChainsManagementControllerProvider;

	@Inject
	private IURIClientManagerDiscovery uriActionDiscovery;

	@Inject
	private TaskChainSearchFieldWidgetContext.Factory taskChainSearchFieldWidgetContextFactory;

	@Inject
	TaskChainSearchDialogContext.Factory taskChainSearchDialogContextFactory;

	@Inject
	private ITodoManagementController todosManagementController;

	@Inject
	public TaskManagerController(ITaskManagerViewFactory viewFactory) {
		super();

		this.viewFactory = viewFactory;
	}

	@Override
	public void initializeFrontendContext(IFrontendContext frontendContext) {
		frontendContext.registerController(this);

		if (StaticTaskManagerHelper.getTaskManagerAuthsBundle().hasReadTaskServicesManagement()) {
			frontendContext.registerController(new TaskServiceDescriptorsManagementController(viewFactory));
		}

		if (StaticTaskManagerHelper.getTaskManagerAuthsBundle().hasReadTaskTypesManagement()) {
			frontendContext.registerController(new TaskTypesManagementController(viewFactory, this));
		}

		if (StaticTaskManagerHelper.getTaskManagerAuthsBundle().hasReadTaskChainsManagement()) {
			taskChainsManagementController = taskChainsManagementControllerProvider.get();
			frontendContext.registerController(taskChainsManagementController);
		}

		if (StaticTaskManagerHelper.getTaskManagerAuthsBundle().hasReadStatusGraphsManagement()) {
			frontendContext.registerController(new StatusGraphsManagementController(viewFactory, this));
		}

		if (StaticTaskManagerHelper.getTaskManagerAuthsBundle().hasReadTasksManagement()) {
			tasksManagementController = new TasksManagementController(viewFactory, this, uriActionDiscovery);
			frontendContext.registerController(tasksManagementController);
			tasksGraphController = new TasksGraphController(viewFactory, this);
			frontendContext.registerController(tasksGraphController);
			simulationTasksGraphController = new SimulationTasksGraphController(viewFactory);
			frontendContext.registerController(simulationTasksGraphController);
		}

		if (StaticTaskManagerHelper.getTaskManagerAuthsBundle().hasReadErrorsManagement()) {
			this.errorsManagementController = new ErrorsManagementController(viewFactory, this);
			frontendContext.registerController(errorsManagementController);
		}

		if (StaticTaskManagerHelper.getTaskManagerAuthsBundle().hasReadTodoFoldersManagement()) {
			frontendContext.registerController(new TodoFoldersManagementController(viewFactory));
		}
	}

	/**
	 * Show and load tasks graph for idTasks
	 */
	public void loadTasksGraph(Serializable idTask) {
		if (tasksGraphController != null) {
			tasksGraphController.loadTasksGraphWithIdTask(idTask);
		}
	}

	@Override
	public ISearchFieldWidgetContext<ITaskServiceDescriptor> getTaskServiceDescriptorSearchFieldWidgetContext(IView parent, Map<String, Object> filters) {
		return new TaskServiceDescriptorSearchFieldWidgetContext(viewFactory, parent, filters);
	}

	@Override
	public ISearchDialogContext<ITaskType> getTaskTypesSearchDialogContext(IView parent, Map<String, Object> filters) {
		return new TaskTypeSearchDialogContext(viewFactory, parent, filters);
	}

	@Override
	public ISearchFieldWidgetContext<ITaskType> getTaskTypeSearchFieldWidgetContext(IView parent, Map<String, Object> filters) {
		return new TaskTypeSearchFieldWidgetContext(viewFactory, parent, filters);
	}

	@Override
	public ISearchDialogContext<ITodoFolder> getTodoFolderSearchDialogContext(IView parent, Map<String, Object> filters) {
		return new TodoFolderSearchDialogContext(viewFactory, parent, filters);
	}

	@Override
	public ISearchFieldWidgetContext<ITodoFolder> getTodoFolderSearchFieldWidgetContext(IView parent, Map<String, Object> filters) {
		return new TodoFolderSearchFieldWidgetContext(viewFactory, parent, filters);
	}

	@Override
	public ISearchDialogContext<ITaskChain> getTaskChainsSearchDialogContext(IView parent, Map<String, Object> filters) {
		return taskChainSearchDialogContextFactory.create(parent, filters);
	}

	@Override
	public <E extends ITaskObject<?>> void searchTasksBy(E taskObject) {
		if (tasksManagementController != null) {
			tasksManagementController.searchTasks(taskObject);
		}
	}

	@Override
	public <E extends ITaskObject<?>> void searchErrorsByObject(E taskObject) {
		if (errorsManagementController != null) {
			errorsManagementController.searchErrors(taskObject);
		}
	}

	@Override
	public void searchErrorsByTask(Serializable idTask) {
		if (errorsManagementController != null) {
			errorsManagementController.searchErrorsByTask(idTask);
		}
	}

	@Override
	public <E extends ITaskObject<?>> void loadTasksGraphByObject(E taskObject) {
		if (tasksGraphController != null) {
			tasksGraphController.loadTasksGraphWithObject(taskObject);
		}
	}

	@Override
	public void showTasksGraphSimulation(Class<? extends ITaskObject<?>> objectType) {
		simulationTasksGraphController.showFilters(objectType);
	}

	public void createNewTaskChain(ITaskType taskType) {
		taskChainsManagementController.createTaskChainFromTaskType(taskType);
	}

	@Override
	public void refreshTodoList(List<Serializable> idObjects) {
		todosManagementController.refresh(idObjects);
	}

	@Override
	public void refreshTodoList(Serializable idObject) {
		ArrayList<Serializable> list = new ArrayList<Serializable>();
		list.add(idObject);
		todosManagementController.refresh(list);
	}

	@Override
	public void reloadTodoDetails() {
		todosManagementController.reloadTodoDetails();
	}

	public void searchTaskByCluster(Serializable idCluster) {
		if (tasksManagementController != null) {
			tasksManagementController.searchTasksByIdCluster(idCluster);
		}
	}

	public void startTaskManager(Serializable idCluster) {
		if (tasksManagementController != null) {
			tasksManagementController.startTaskManager(idCluster);
		}
	}

	@Override
	public ISearchFieldWidgetContext<ITaskChain> getTaskChainsearchFieldWidgetContext(IView parent, Map<String, Object> filters) {
		return taskChainSearchFieldWidgetContextFactory.create(parent, filters);
	}

	@Override
	public void loadTodoFoldersList() {
		todosManagementController.loadTodoFoldersList();
	}

}
