package com.synaptix.taskmanager.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

import com.synaptix.client.view.IView;
import com.synaptix.client.view.IWaitWorker;
import com.synaptix.service.IEntityService;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.taskmanager.model.IAssoTaskPreviousTask;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.service.ITasksService;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.taskmanager.view.ITasksGraphView;
import com.synaptix.widget.core.controller.AbstractController;
import com.synaptix.widget.viewworker.view.AbstractLoadingViewWorker;

public class TasksGraphController extends AbstractController {

	private final ITaskManagerViewFactory viewFactory;
	private TaskManagerController taskManagerController;

	private ITasksGraphView tasksGraphView;

	private IWaitWorker loadTasksGraphWaitWorker;
	private ITaskObject<?> taskObject;

	public TasksGraphController(ITaskManagerViewFactory viewFactory, TaskManagerController taskManagerController) {
		super();

		this.viewFactory = viewFactory;
		this.taskManagerController = taskManagerController;

		initialize();
	}

	private void initialize() {
		tasksGraphView = viewFactory.newTaskGraphView(this);
	}

	@Override
	public IView getView() {
		return tasksGraphView;
	}

	private IServiceFactory getPscNormalServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("psc-normal");
	}

	private IEntityService getEntityService() {
		return getPscNormalServiceFactory().getService(IEntityService.class);
	}

	private ITasksService getTaskService() {
		return getPscNormalServiceFactory().getService(ITasksService.class);
	}

	/**
	 * Load tasks graph
	 */
	public void loadTasksGraphWithIdTask(final Serializable idTask) {
		if (loadTasksGraphWaitWorker != null && !loadTasksGraphWaitWorker.isDone()) {
			loadTasksGraphWaitWorker.cancel(false);
			loadTasksGraphWaitWorker = null;
		}
		tasksGraphView.showView();
		if (idTask != null) {
			loadTasksGraphWaitWorker = viewFactory.waitFullComponentViewWorker(getView(), new AbstractLoadingViewWorker<LoadTasksGraphResult>() {
				@Override
				protected LoadTasksGraphResult doLoading() throws Exception {
					LoadTasksGraphResult res = new LoadTasksGraphResult();
					res.tasks = new ArrayList<ITask>();
					res.assoTaskPreviousTasks = new ArrayList<IAssoTaskPreviousTask>();
					ITask task = getEntityService().findEntityById(ITask.class, idTask);
					if (task != null) {
						res.tasks.addAll(getTaskService().findTasksByCluster(task.getIdCluster()));
						res.assoTaskPreviousTasks.addAll(getTaskService().findAssoTaskPreviousTasksByCluster(task.getIdCluster()));
					}
					return res;
				}

				@Override
				public void success(LoadTasksGraphResult e) {
					tasksGraphView.setTasks(e.tasks, e.assoTaskPreviousTasks);
				}

				@Override
				public void fail(Throwable t) {
					if (!(t instanceof CancellationException)) {
						viewFactory.showErrorMessageDialog(getView(), t);
						tasksGraphView.setTasks(null, null);
					}
				}
			});
		} else {
			tasksGraphView.setTasks(null, null);
		}
	}

	/**
	 * Load tasks graph
	 */
	public <E extends ITaskObject<?>> void loadTasksGraphWithObject(final E taskObject) {
		this.taskObject = taskObject;
		if (loadTasksGraphWaitWorker != null && !loadTasksGraphWaitWorker.isDone()) {
			loadTasksGraphWaitWorker.cancel(false);
			loadTasksGraphWaitWorker = null;
		}
		tasksGraphView.showView();
		if (taskObject != null) {
			loadTasksGraphWaitWorker = viewFactory.waitFullComponentViewWorker(getView(), new AbstractLoadingViewWorker<LoadTasksGraphResult>() {
				@Override
				protected LoadTasksGraphResult doLoading() throws Exception {
					LoadTasksGraphResult res = new LoadTasksGraphResult();
					res.tasks = new ArrayList<ITask>();
					res.assoTaskPreviousTasks = new ArrayList<IAssoTaskPreviousTask>();
					res.tasks.addAll(getTaskService().findTasksByCluster(taskObject.getIdCluster()));
					res.assoTaskPreviousTasks.addAll(getTaskService().findAssoTaskPreviousTasksByCluster(taskObject.getIdCluster()));
					return res;
				}

				@Override
				public void success(LoadTasksGraphResult e) {
					tasksGraphView.setTasks(e.tasks, e.assoTaskPreviousTasks);
				}

				@Override
				public void fail(Throwable t) {
					if (!(t instanceof CancellationException)) {
						viewFactory.showErrorMessageDialog(getView(), t);
						tasksGraphView.setTasks(null, null);
					}
				}
			});
		} else {
			tasksGraphView.setTasks(null, null);
		}
	}

	public void showErrors() {
		taskManagerController.searchErrorsByObject(taskObject);
	}

	public void showErrors(Serializable idTask) {
		taskManagerController.searchErrorsByTask(idTask);
	}

	private final class LoadTasksGraphResult {

		List<ITask> tasks;

		List<IAssoTaskPreviousTask> assoTaskPreviousTasks;

	}

	public void showTasks(Serializable idCluster) {
		taskManagerController.searchTaskByCluster(idCluster);
	}

	public void startTaskManager(Serializable idCluster) {
		taskManagerController.startTaskManager(idCluster);
	}

}
