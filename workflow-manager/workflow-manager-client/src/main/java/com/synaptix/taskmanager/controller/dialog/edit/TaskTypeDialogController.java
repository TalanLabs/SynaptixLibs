package com.synaptix.taskmanager.controller.dialog.edit;

import com.synaptix.common.util.IResultCallback;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.taskmanager.controller.ITaskManagerController;
import com.synaptix.taskmanager.model.ITaskServiceDescriptor;
import com.synaptix.taskmanager.model.ITaskType;
import com.synaptix.taskmanager.service.ITaskServiceDescriptorService;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.widget.component.controller.dialog.AbstractCRUDDialogController;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;
import com.synaptix.widget.view.dialog.ICRUDBeanDialogView;
import com.synaptix.widget.viewworker.view.AbstractLoadingViewWorker;

public class TaskTypeDialogController extends AbstractCRUDDialogController<ITaskType> {

	private final ITaskManagerViewFactory viewFactory;

	private final ITaskManagerController taskManagerController;

	private IBeanExtensionDialogView<ITaskType> generalTaskTypeExtensionBeanDialogView;

	private ICRUDBeanDialogView<ITaskType> beanDialogView;

	public TaskTypeDialogController(ITaskManagerViewFactory viewFactory, ITaskManagerController taskManagerController) {
		super(ITaskType.class, StaticHelper.getTaskManagerConstantsBundle().taskType(), StaticHelper.getTaskManagerConstantsBundle().addTaskType(), StaticHelper.getTaskManagerConstantsBundle()
				.editTaskType());
		this.viewFactory = viewFactory;
		this.taskManagerController = taskManagerController;
		initialize();
	}

	@SuppressWarnings("unchecked")
	private void initialize() {
		generalTaskTypeExtensionBeanDialogView = viewFactory.newGeneralTaskTypeBeanExtensionDialogView(this, taskManagerController);
		beanDialogView = viewFactory.newCRUDBeanDialogView(generalTaskTypeExtensionBeanDialogView);
	}

	private final IServiceFactory getPscNormalServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("psc-normal");
	}

	private final ITaskServiceDescriptorService getTaskServiceDescriptorService() {
		return getPscNormalServiceFactory().getService(ITaskServiceDescriptorService.class);
	}

	/**
	 * Load task service descriptor by service code
	 * 
	 * @param serviceCode
	 * @param resultCallback
	 */
	public void loadTaskServiceDescriptor(final String serviceCode, final IResultCallback<ITaskServiceDescriptor> resultCallback) {
		if (serviceCode != null) {
			viewFactory.waitFullComponentViewWorker(beanDialogView, new AbstractLoadingViewWorker<ITaskServiceDescriptor>() {
				@Override
				protected ITaskServiceDescriptor doLoading() throws Exception {
					return getTaskServiceDescriptorService().findTaskServiceDescriptorByCode(serviceCode);
				}

				@Override
				public void success(ITaskServiceDescriptor e) {
					resultCallback.setResult(e);
				}

				@Override
				public void fail(Throwable t) {
					viewFactory.showErrorMessageDialog(beanDialogView, t);
					resultCallback.setResult(null);
				}
			});
		} else {
			resultCallback.setResult(null);
		}
	}

	@Override
	protected ICRUDBeanDialogView<ITaskType> getCRUDBeanDialogView() {
		return beanDialogView;
	}
}
