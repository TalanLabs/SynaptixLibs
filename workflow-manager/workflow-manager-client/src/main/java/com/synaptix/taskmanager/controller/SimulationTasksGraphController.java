package com.synaptix.taskmanager.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

import com.synaptix.client.view.IView;
import com.synaptix.client.view.IWaitWorker;
import com.synaptix.service.IComponentService;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.taskmanager.helper.TaskManagerHelper;
import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITaskChainCriteria;
import com.synaptix.taskmanager.model.ITaskManagerContext;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.objecttype.IObjectType;
import com.synaptix.taskmanager.service.IStatusGraphService;
import com.synaptix.taskmanager.service.ITaskChainCriteriaService;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.taskmanager.view.ITasksGraphSimulationView;
import com.synaptix.widget.core.controller.AbstractController;
import com.synaptix.widget.viewworker.view.AbstractLoadingViewWorker;


public class SimulationTasksGraphController extends AbstractController {

	private final ITaskManagerViewFactory viewFactory;

	private ITasksGraphSimulationView tasksGraphView;

	private IWaitWorker loadTasksGraphWaitWorker;

	public SimulationTasksGraphController(ITaskManagerViewFactory viewFactory) {
		super();

		this.viewFactory = viewFactory;

		initialize();
	}

	private void initialize() {
		tasksGraphView = viewFactory.newTaskGraphSimulationView(this);
	}

	@Override
	public IView getView() {
		return tasksGraphView;
	}

	protected final IServiceFactory getPscNormalServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("psc-normal");
	}

	protected final IComponentService getComponentService() {
		return getPscNormalServiceFactory().getService(IComponentService.class);
	}

	protected final ITaskChainCriteriaService getTaskChainCriteriaService() {
		return getPscNormalServiceFactory().getService(ITaskChainCriteriaService.class);
	}

	protected final IStatusGraphService getStatusGraphService() {
		return getPscNormalServiceFactory().getService(IStatusGraphService.class);
	}

	public void showFilters(final Class<? extends ITaskObject<?>> objectTypeClass) {
		IObjectType<?, ?, ?, ?> objectType = TaskManagerHelper.getObjectTypeManager().getObjectType2(objectTypeClass);
		if (objectType != null) {
			IPreviewTaskManagerContextDialogController previewTaskManagerContextDialogController = objectType.newPreviewContextTemplateEditorDialogController();
			if (previewTaskManagerContextDialogController != null) {
				final Object taskManagerContext = previewTaskManagerContextDialogController.newContext(getView());
				if (taskManagerContext != null) {
					loadTasksGraphWithFilters((ITaskManagerContext) taskManagerContext, objectTypeClass);
				}
			}
		}
	}

	public void editFilters(ITaskManagerContext context, final Class<? extends ITaskObject<?>> objectTypeClass) {
		IObjectType<?, ?, ?, ?> objectType = TaskManagerHelper.getObjectTypeManager().getObjectType2(objectTypeClass);
		if (objectType != null) {
			IPreviewTaskManagerContextDialogController previewTaskManagerContextDialogController = objectType.newPreviewContextTemplateEditorDialogController();
			if (previewTaskManagerContextDialogController != null) {
				final Object taskManagerContext = previewTaskManagerContextDialogController.edit(getView(), context);
				if (taskManagerContext != null) {
					loadTasksGraphWithFilters((ITaskManagerContext) taskManagerContext, objectTypeClass);
				}
			}
		}
	}

	public void loadTasksGraphWithFilters(final ITaskManagerContext context, final Class<? extends ITaskObject<?>> objectType) {
		if (loadTasksGraphWaitWorker != null && !loadTasksGraphWaitWorker.isDone()) {
			loadTasksGraphWaitWorker.cancel(false);
			loadTasksGraphWaitWorker = null;
		}
		tasksGraphView.showView();
		loadTasksGraphWaitWorker = viewFactory.waitFullComponentViewWorker(getView(), new AbstractLoadingViewWorker<LoadTasksGraphResult>() {

			@Override
			protected LoadTasksGraphResult doLoading() throws Exception {
				LoadTasksGraphResult res = new LoadTasksGraphResult();

				List<IStatusGraph> statusGraphs = getStatusGraphService().findStatusGraphsBy(objectType);
				List<ITaskChainCriteria<? extends Enum<?>>> taskChainCriteriaList = new ArrayList<ITaskChainCriteria<? extends Enum<?>>>();
				for (IStatusGraph sg : statusGraphs) {
					taskChainCriteriaList.add(getTaskChainCriteriaService().getTaskChainCriteria(context, sg.getCurrentStatus(), sg.getNextStatus(), objectType));
				}
				res.statusGraphs = statusGraphs;
				res.taskChainCriterias = taskChainCriteriaList;
				res.context = context;
				res.objectType = objectType;
				return res;
			}

			@Override
			public void success(LoadTasksGraphResult e) {
				tasksGraphView.updateSimulation(e.statusGraphs, e.taskChainCriterias, e.context, e.objectType);
			}

			@Override
			public void fail(Throwable t) {
				if (!(t instanceof CancellationException)) {
					viewFactory.showErrorMessageDialog(getView(), t);
					tasksGraphView.updateSimulation(null, null, null, objectType);
				}
			}
		});
	}

	protected final class LoadTasksGraphResult {

		List<ITaskChainCriteria<? extends Enum<?>>> taskChainCriterias;

		List<IStatusGraph> statusGraphs;

		ITaskManagerContext context;

		Class<? extends ITaskObject<?>> objectType;

	}
}
