package com.synaptix.taskmanager.controller;

import java.util.Map;

import com.synaptix.entity.IId;
import com.synaptix.entity.IdRaw;
import com.synaptix.taskmanager.controller.helper.AbstractWorkflowComponentsManagementController;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.TaskFields;
import com.synaptix.taskmanager.service.ITaskManagerService;
import com.synaptix.taskmanager.urimanager.IURIClientManagerDiscovery;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.taskmanager.view.descriptor.ITasksManagementViewDescriptor;
import com.synaptix.widget.component.view.IComponentsManagementViewDescriptor;
import com.synaptix.widget.viewworker.view.AbstractLoadingViewWorker;
import com.synaptix.widget.viewworker.view.AbstractSavingViewWorker;

public class TasksManagementController extends AbstractWorkflowComponentsManagementController<ITaskManagerViewFactory, ITask> {

	private final TaskManagerController taskManagerController;

	private ITasksManagementViewDescriptor tasksManagementViewDescriptor;

	private IURIClientManagerDiscovery uriActionDiscovery;

	public TasksManagementController(ITaskManagerViewFactory viewFactory, TaskManagerController taskManagerController, IURIClientManagerDiscovery uriActionDiscovery2) {
		super(viewFactory, ITask.class);

		this.taskManagerController = taskManagerController;
		this.uriActionDiscovery = uriActionDiscovery2;
	}

	@Override
	protected IComponentsManagementViewDescriptor<ITask> createComponentsManagementViewDescriptor() {
		return getViewFactory().createTaskViewDescriptor(this);
	}

	@Override
	protected void completeFilters(Map<String, Object> filters) {
		super.completeFilters(filters);

		Object value = filters.get(TaskFields.idObject().name());
		if (value instanceof String && !((String) value).isEmpty()) {
			filters.put(TaskFields.idObject().name(), new IdRaw((String) value));
		}

		Object valueCluster = filters.get(TaskFields.idCluster().name());
		if (valueCluster instanceof String && !((String) valueCluster).isEmpty()) {
			filters.put(TaskFields.idCluster().name(), new IdRaw((String) valueCluster));
		}
	}

	private ITaskManagerService getTaskManagerService() {
		return getServiceFactory().getService(ITaskManagerService.class);
	}

	/**
	 * Skip a task and start engine
	 */
	public void skipTask(final ITask paginationTask) {
		final String comments = getViewFactory().addEditCommentaireDialog(getView(), StaticHelper.getTaskTableConstantsBundle().skipTask(), null, 2000, false);
		if (comments != null && !comments.trim().isEmpty()) {
			getViewFactory().waitFullComponentViewWorker(getView(), new AbstractSavingViewWorker<Boolean>() {
				@Override
				protected Boolean doSaving() throws Exception {
					return getTaskManagerService().skipTask(paginationTask.getId(), comments);
				}

				@Override
				public void success(Boolean e) {
					loadPagination();
				}

				@Override
				public void fail(Throwable t) {
					getViewFactory().showErrorMessageDialog(getView(), t);
				}
			});
		}
	}

	/**
	 * Show tasks graph
	 */
	public void showTasksGraph(ITask paginationTask) {
		taskManagerController.loadTasksGraph(paginationTask.getId());
	}

	/**
	 * Search tasks with TaskObject
	 */
	public <E extends ITaskObject<?>> void searchTasks(E taskObject) {
		cancelWork();
		tasksManagementViewDescriptor.searchBy(taskObject);
	}

	public void showErrors(ITask task) {
		taskManagerController.searchErrorsByTask(task.getId());
	}

	public void searchTasksByIdCluster(IId idCluster) {
		tasksManagementViewDescriptor.searchByCluster(idCluster);
	}

	public void startTaskManager(final IId idCluster) {
		getViewFactory().waitFullComponentViewWorker(getView(), new AbstractLoadingViewWorker<Boolean>() {

			@Override
			protected Boolean doLoading() throws Exception {
				getTaskManagerService().startEngine(idCluster);
				return true;
			}

			@Override
			public void success(Boolean arg0) {
				updatePagination();

			}

			@Override
			public void fail(Throwable t) {
				getViewFactory().showErrorMessageDialog(getView(), t);

			}

		});

	}

}
