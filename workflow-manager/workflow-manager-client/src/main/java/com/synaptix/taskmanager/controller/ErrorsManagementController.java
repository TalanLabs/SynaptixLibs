package com.synaptix.taskmanager.controller;

import java.util.Map;

import com.synaptix.entity.ErrorEntityFields;
import com.synaptix.entity.IErrorEntity;
import com.synaptix.entity.IId;
import com.synaptix.entity.IWithError;
import com.synaptix.entity.IdRaw;
import com.synaptix.service.IEntityService;
import com.synaptix.taskmanager.controller.helper.AbstractWorkflowComponentsManagementController;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.service.ITaskManagerService;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.taskmanager.view.descriptor.IErrorsManagementViewDescriptor;
import com.synaptix.widget.component.view.IComponentsManagementViewDescriptor;


public class ErrorsManagementController extends AbstractWorkflowComponentsManagementController<ITaskManagerViewFactory, IErrorEntity> {

	private final TaskManagerController taskManagerController;

	private IErrorsManagementViewDescriptor errorsManagementViewDescriptor;

	// private IWaitWorker loadHistoriesWaitWorker;

	public ErrorsManagementController(ITaskManagerViewFactory viewFactory, TaskManagerController taskManagerController) {
		super(viewFactory, IErrorEntity.class);

		this.taskManagerController = taskManagerController;
	}

	@Override
	protected IComponentsManagementViewDescriptor<IErrorEntity> createComponentsManagementViewDescriptor() {
		errorsManagementViewDescriptor = getViewFactory().createErrorsViewDescriptor(this);
		return errorsManagementViewDescriptor;
	}

	private final ITaskManagerService getTaskManagerService() {
		return getServiceFactory().getService(ITaskManagerService.class);
	}

	@Override
	protected void completeFilters(Map<String, Object> filters) {
		super.completeFilters(filters);

		Object value = filters.get(ErrorEntityFields.idObject().name());
		if (value instanceof String && !((String) value).isEmpty()) {
			filters.put(ErrorEntityFields.idObject().name(), new IdRaw((String) value));
		}

		Object valueTask = filters.get(ErrorEntityFields.idTask().name());
		if (valueTask instanceof String && !((String) valueTask).isEmpty()) {
			filters.put(ErrorEntityFields.idTask().name(), new IdRaw((String) valueTask));
		}
	}

	/**
	 * Search errors for a TaskObject
	 * 
	 * @param taskObject
	 *            ITaskObject
	 */
	public <E extends ITaskObject<?>> void searchErrors(E taskObject) {
		cancelWork();
		errorsManagementViewDescriptor.searchByObject(taskObject);
	}

	/**
	 * Search errors for a Task.
	 * 
	 * @param idTask
	 *            ID of the task.
	 */
	public void searchErrorsByTask(IId idTask) {
		cancelWork();
		errorsManagementViewDescriptor.searchByTask(idTask);
	}

	public void searchTasks(IId idObject, Class<? extends IWithError> clazz) {
		ITaskObject<?> taskObject = (ITaskObject<?>) getServiceFactory().getService(IEntityService.class).findEntityById(clazz, idObject);
		taskManagerController.searchTasksBy(taskObject);
	}
	// public void loadTaskHistories(final ITask paginationTask) {
	// if (loadHistoriesWaitWorker != null && !loadHistoriesWaitWorker.isDone()) {
	// loadHistoriesWaitWorker.cancel(false);
	// loadHistoriesWaitWorker = null;
	// }
	// if (paginationTask != null) {
	// loadHistoriesWaitWorker = getViewFactory().waitComponentViewWorker(getView(), new AbstractLoadingViewWorker<List<ITaskHistory>>() {
	// @Override
	// protected List<ITaskHistory> doLoading() throws Exception {
	// return getComponentService().findComponentsByIdParent(ITaskHistory.class, TaskHistoryFields.idTask().name(), paginationTask.getId());
	// }
	//
	// @Override
	// public void success(List<ITaskHistory> e) {
	// tasksManagementViewDescriptor.setTaskHistories(e);
	// }
	//
	// @Override
	// public void fail(Throwable t) {
	// if (!(t instanceof CancellationException)) {
	// getViewFactory().showErrorMessageDialog(getView(), t);
	// tasksManagementViewDescriptor.setTaskHistories(null);
	// }
	// }
	// });
	// } else {
	// tasksManagementViewDescriptor.setTaskHistories(null);
	// }
	// }
	//
	// /**
	// * Skip a task and start engine
	// *
	// * @param paginationTask
	// */
	// public void skipTask(final ITask paginationTask) {
	// final String comments = getViewFactory().addEditCommentaireDialog(getView(), StaticHelper.getTaskTableConstantsBundle().skipTask(), null, 2000, false);
	// if (comments != null && !comments.trim().isEmpty()) {
	// getViewFactory().waitFullComponentViewWorker(getView(), new AbstractSavingViewWorker<Boolean>() {
	// @Override
	// protected Boolean doSaving() throws Exception {
	// return getTaskManagerService().skipTask(paginationTask.getId(), comments);
	// }
	//
	// @Override
	// public void success(Boolean e) {
	// loadPagination();
	// }
	//
	// @Override
	// public void fail(Throwable t) {
	// getViewFactory().showErrorMessageDialog(getView(), t);
	// }
	// });
	// }
	// }
	//
	// /**
	// * Show tasks graph
	// *
	// * @param paginationTask
	// */
	// public void showTasksGraph(ITask paginationTask) {
	// taskManagerController.loadTasksGraph(paginationTask.getId());
	// }
	//
	// /**
	// * Search tasks with TaskObject
	// *
	// * @param taskObject
	// */
	// public <E extends ITaskObject<?>> void searchBy(E taskObject) {
	// cancelWork();
	// tasksManagementViewDescriptor.searchBy(taskObject);
	// }
}
