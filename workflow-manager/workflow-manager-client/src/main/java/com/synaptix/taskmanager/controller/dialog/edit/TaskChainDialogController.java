package com.synaptix.taskmanager.controller.dialog.edit;

import com.synaptix.taskmanager.controller.ITaskManagerController;
import com.synaptix.taskmanager.model.ITaskChain;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.widget.component.controller.dialog.AbstractCRUDDialogController;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;
import com.synaptix.widget.view.dialog.ICRUDBeanDialogView;

public class TaskChainDialogController extends AbstractCRUDDialogController<ITaskChain> {

	private final ITaskManagerViewFactory viewFactory;

	private final ITaskManagerController taskManagerController;

	private ICRUDBeanDialogView<ITaskChain> beanDialogView;

	public TaskChainDialogController(ITaskManagerViewFactory viewFactory, ITaskManagerController taskManagerController) {
		super(ITaskChain.class, StaticHelper.getTaskManagerConstantsBundle().taskChain(), StaticHelper.getTaskManagerConstantsBundle().addTaskChain(), StaticHelper.getTaskManagerConstantsBundle()
				.editTaskChain());
		this.viewFactory = viewFactory;
		this.taskManagerController = taskManagerController;
		initialize();
	}

	@SuppressWarnings("unchecked")
	private void initialize() {
		IBeanExtensionDialogView<ITaskChain> generalTaskChainExtensionBeanDialogView = viewFactory.newGeneralTaskChainBeanExtensionDialogView(taskManagerController);
		beanDialogView = viewFactory.newCRUDBeanDialogView(generalTaskChainExtensionBeanDialogView);
	}

	@Override
	protected ICRUDBeanDialogView<ITaskChain> getCRUDBeanDialogView() {
		return beanDialogView;
	}
}
