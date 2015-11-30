package com.synaptix.taskmanager.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

import com.google.inject.Inject;
import com.synaptix.client.common.controller.AbstractPscSimpleCRUDManagementController;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.client.view.IWaitWorker;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.IdRaw;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServiceException;
import com.synaptix.service.ServicesManager;
import com.synaptix.taskmanager.antlr.GraphCalcHelper;
import com.synaptix.taskmanager.controller.dialog.edit.TaskChainDialogController;
import com.synaptix.taskmanager.helper.StaticTaskManagerHelper;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.service.ITaskChainService;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.taskmanager.view.descriptor.ITaskChainsManagementViewDescriptor;
import com.synaptix.widget.component.controller.dialog.ICRUDDialogController;
import com.synaptix.widget.crud.view.descriptor.ICRUDManagementViewDescriptor;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.viewworker.view.AbstractLoadingViewWorker;
import com.synaptix.widget.viewworker.view.AbstractSavingViewWorker;

public class TaskChainsManagementController extends AbstractPscSimpleCRUDManagementController<ITaskManagerViewFactory, ITaskChain> {

	public static final String UNICITY_CONSTRAINT = "unicityConstraint";

	private final ITaskManagerController taskManagerController;

	private IWaitWorker loadTaskChainWaitWorker;

	private ITaskChainsManagementViewDescriptor taskChainsManagementViewDescriptor;

	@Inject
	public TaskChainsManagementController(ITaskManagerViewFactory viewFactory, ITaskManagerController taskManagerController) {
		super(viewFactory, ITaskChain.class, ITaskChainService.class, null);

		this.taskManagerController = taskManagerController;
	}

	@Override
	protected ICRUDManagementViewDescriptor<ITaskChain> createCRUDManagementViewDescriptor() {
		taskChainsManagementViewDescriptor = getViewFactory().createTaskChainViewDescriptor(this);
		return taskChainsManagementViewDescriptor;
	}

	@Override
	public boolean hasAuthWrite() {
		return StaticTaskManagerHelper.getTaskManagerAuthsBundle().hasWriteTaskChainsManagement();
	}

	@Override
	protected ICRUDDialogController<ITaskChain> newCRUDDialogController(DialogAction dialogAction, ITaskChain entity) {
		return new TaskChainDialogController(getViewFactory(), taskManagerController);
	}

	private final IServiceFactory getPscNormalServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("psc-normal");
	}

	private final ITaskChainService getTaskChainService() {
		return getPscNormalServiceFactory().getService(ITaskChainService.class);
	}

	/**
	 * Load a task chain
	 * 
	 * @param vat
	 */
	public void loadTaskChain(final ITaskChain paginationTaskChain) {
		if (loadTaskChainWaitWorker != null && !loadTaskChainWaitWorker.isDone()) {
			loadTaskChainWaitWorker.cancel(false);
			loadTaskChainWaitWorker = null;
		}
		if (paginationTaskChain != null) {
			loadTaskChainWaitWorker = getViewFactory().waitComponentViewWorker(getSearchComponentsView(), new AbstractLoadingViewWorker<ITaskChain>() {

				@Override
				protected ITaskChain doLoading() throws Exception {
					return getEntityService().findEntityById(ITaskChain.class, paginationTaskChain.getId());
				}

				@Override
				public void success(ITaskChain e) {
					taskChainsManagementViewDescriptor.setTaskChain(e);
				}

				@Override
				public void fail(Throwable t) {
					if (!(t instanceof CancellationException)) {
						getViewFactory().showErrorMessageDialog(getSearchComponentsView(), t);
						taskChainsManagementViewDescriptor.setTaskChain(null);
					}
				}
			});
		} else {
			taskChainsManagementViewDescriptor.setTaskChain(null);
		}
	}

	public void createTaskChainFromTaskType(final ITaskType taskType) {
		ITaskChain taskChain = ComponentFactory.getInstance().createInstance(ITaskChain.class);
		taskChain.setObjectType(taskType.getObjectType());
		List<ITaskType> taskTypes = new ArrayList<ITaskType>();
		taskTypes.add(taskType);
		taskChain.setTaskTypes(taskTypes);
		taskChain.setDescription(taskType.getDescription());
		taskChain.setCode(taskType.getCode());
		taskChain.setGraphRuleReadable(taskType.getCode());
		taskChain.setGraphRule(taskType.getCode() != null ? GraphCalcHelper.replaceId(taskType.getCode(), new GraphCalcHelper.IReplaceId() {
			@Override
			public String getOtherId(String id) {
				return taskType != null ? ((IdRaw) taskType.getId()).getHex() : id;
			}
		}) : null);

		TaskChainDialogController taskChainDialogController = new TaskChainDialogController(getViewFactory(), taskManagerController);
		taskChainDialogController.editEntity(getView(), taskChain, new IResultCallback<ITaskChain>() {

			@Override
			public void setResult(final ITaskChain e) {
				getViewFactory().waitFullComponentViewWorker(getView(), new AbstractLoadingViewWorker<Serializable>() {
					@Override
					protected Serializable doLoading() throws Exception {
						return getTaskChainService().addCRUDEntity(e);
					}

					@Override
					public void success(Serializable e) {
					}

					@Override
					public void fail(Throwable t) {
						if (t.getCause() instanceof ServiceException || t instanceof ServiceException && UNICITY_CONSTRAINT.equals(((ServiceException) t).getCode())) {
							getViewFactory().showInformationMessageDialog(getView(), StaticHelper.getTaskChainTableConstantsBundle().error(),
									StaticHelper.getTaskChainTableConstantsBundle().alreadyExists());
						} else {
							getViewFactory().showErrorMessageDialog(getView(), t);
						}
					}
				});
			}
		});
	}

	@Override
	public void deleteEntity(ITaskChain paginationEntity) {
		if (getViewFactory().showQuestionMessageDialog(getView(), StaticWidgetHelper.getSynaptixWidgetConstantsBundle().validation(),
				StaticCommonHelper.getCommonConstantsBundle().doYouWantToDeleteTheSelectedItem())) {
			loadEntity(paginationEntity.getId(), new IResultCallback<ITaskChain>() {
				@Override
				public void setResult(final ITaskChain taskChain) {
					if (taskChain != null) {
						getViewFactory().waitFullComponentViewWorker(getView(), new AbstractSavingViewWorker<Serializable>() {
							@Override
							protected Serializable doSaving() throws Exception {
								if (getTaskChainService().getTaskChainCriteriasFromTaskChain(taskChain))
									return null;
								return getEntityService().removeEntity(taskChain);
							}

							@Override
							public void success(Serializable e) {
								if (e == null) {
									getViewFactory().showInformationMessageDialog(getView(), StaticCommonHelper.getCommonConstantsBundle().error(),
											StaticHelper.getTaskChainTableConstantsBundle().isTaskChainCriteriaExisted());
								}
								updatePagination();
							}

							@Override
							public void fail(Throwable t) {
								getViewFactory().showErrorMessageDialog(getView(), t);
							}
						});
					}
				}
			});
		}
	}
}
