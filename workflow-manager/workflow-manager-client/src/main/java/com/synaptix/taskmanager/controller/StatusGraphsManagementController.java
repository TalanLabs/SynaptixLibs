package com.synaptix.taskmanager.controller;

import java.util.List;

import com.synaptix.client.view.IView;
import com.synaptix.taskmanager.controller.dialog.edit.StatusGraphDialogController;
import com.synaptix.taskmanager.controller.helper.AbstractWorkflowSimpleCRUDController;
import com.synaptix.taskmanager.helper.StaticTaskManagerHelper;
import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.service.IStatusGraphService;
import com.synaptix.taskmanager.view.IStatusGraphsManagementView;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.widget.component.controller.dialog.ICRUDDialogController;
import com.synaptix.widget.viewworker.view.AbstractLoadingViewWorker;


public class StatusGraphsManagementController extends AbstractWorkflowSimpleCRUDController<ITaskManagerViewFactory, IStatusGraph> {

	private final ITaskManagerController taskManagerController;

	private IStatusGraphsManagementView statucGraphsManagementView;

	private Class<? extends ITaskObject<?>> currentTaskObjectClass;

	public StatusGraphsManagementController(ITaskManagerViewFactory viewFactory, ITaskManagerController taskManagerController) {
		super(viewFactory, IStatusGraph.class);

		this.taskManagerController = taskManagerController;

		initialize();
	}

	private void initialize() {
		statucGraphsManagementView = getViewFactory().newStatusGraphsManagementView(this);
	}

	@Override
	public IView getView() {
		return statucGraphsManagementView;
	}

	private IStatusGraphService getStatusGraphService() {
		return getServiceFactory().getService(IStatusGraphService.class);
	}

	@Override
	public boolean hasAuthWrite() {
		return StaticTaskManagerHelper.getTaskManagerAuthsBundle().hasWriteStatusGraphsManagement();
	}

	@Override
	protected ICRUDDialogController<IStatusGraph> newCRUDDialogController() {
		return new StatusGraphDialogController(getViewFactory(), taskManagerController, currentTaskObjectClass);
	}

	@Override
	protected void loadEntities() {
		statucGraphsManagementView.refresh();
	}

	public void loadStatusGraphs(final Class<? extends ITaskObject<?>> taskObjectClass) {
		this.currentTaskObjectClass = taskObjectClass;
		if (taskObjectClass != null) {
			getViewFactory().waitFullComponentViewWorker(getView(), new AbstractLoadingViewWorker<List<IStatusGraph>>() {
				@Override
				protected List<IStatusGraph> doLoading() throws Exception {
					return getStatusGraphService().findStatusGraphsBy(taskObjectClass);
				}

				@Override
				public void success(List<IStatusGraph> e) {
					statucGraphsManagementView.setStatusGraphs(e);
				}

				@Override
				public void fail(Throwable t) {
					getViewFactory().showErrorMessageDialog(getView(), t);
					statucGraphsManagementView.setStatusGraphs(null);
				}
			});
		} else {
			statucGraphsManagementView.setStatusGraphs(null);
		}
	}
}
