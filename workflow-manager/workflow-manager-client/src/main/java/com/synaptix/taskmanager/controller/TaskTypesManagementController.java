package com.synaptix.taskmanager.controller;

import java.io.Serializable;

import com.synaptix.client.common.controller.AbstractPscSimpleNlsCRUDManagementController;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.taskmanager.controller.dialog.edit.TaskTypeDialogController;
import com.synaptix.taskmanager.helper.StaticTaskManagerHelper;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.service.ITaskChainService;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.widget.component.controller.dialog.ICRUDDialogController;
import com.synaptix.widget.crud.view.descriptor.ICRUDManagementViewDescriptor;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.viewworker.view.AbstractSavingViewWorker;

public class TaskTypesManagementController extends AbstractPscSimpleNlsCRUDManagementController<ITaskManagerViewFactory, ITaskType> {

	private final TaskManagerController taskManagerController;

	public TaskTypesManagementController(ITaskManagerViewFactory viewFactory, TaskManagerController taskManagerController) {
		super(viewFactory, ITaskType.class);

		this.taskManagerController = taskManagerController;
	}

	@Override
	protected ICRUDManagementViewDescriptor<ITaskType> createCRUDManagementViewDescriptor() {
		return getViewFactory().createTaskTypeViewDescriptor(this);
	}

	@Override
	public boolean hasAuthWrite() {
		return StaticTaskManagerHelper.getTaskManagerAuthsBundle().hasWriteTaskTypesManagement();
	}

	@Override
	protected ICRUDDialogController<ITaskType> newCRUDDialogController(DialogAction dialogAction, ITaskType entity) {
		return new TaskTypeDialogController(getViewFactory(), taskManagerController);
	}

	public void createNewTaskChain(ITaskType taskType) {
		taskManagerController.createNewTaskChain(taskType);
	}

	@Override
	public void deleteEntity(ITaskType paginationEntity) {
		if (getViewFactory().showQuestionMessageDialog(getView(), StaticWidgetHelper.getSynaptixWidgetConstantsBundle().validation(),
				StaticCommonHelper.getCommonConstantsBundle().doYouWantToDeleteTheSelectedItem())) {
			loadEntity(paginationEntity.getId(), new IResultCallback<ITaskType>() {
				@Override
				public void setResult(final ITaskType taskType) {
					if (taskType != null) {
						getViewFactory().waitFullComponentViewWorker(getView(), new AbstractSavingViewWorker<Serializable>() {
							@Override
							protected Serializable doSaving() throws Exception {
								if (getTaskChainService().getTaskChainsFromTaskType(taskType))
									return null;
								return getEntityService().removeEntity(taskType);
							}

							@Override
							public void success(Serializable e) {
								if (e == null) {
									getViewFactory().showInformationMessageDialog(getView(), StaticCommonHelper.getCommonConstantsBundle().error(),
											StaticHelper.getTaskTypeTableConstantsBundle().isTaskChainExisted());
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

	private final ITaskChainService getTaskChainService() {
		return getServiceFactory().getService(ITaskChainService.class);
	}
}
