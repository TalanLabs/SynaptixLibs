package com.synaptix.taskmanager.controller.dialog.edit;

import com.synaptix.taskmanager.model.IStatusGraph;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.taskmanager.controller.ITaskManagerController;
import com.synaptix.widget.component.controller.dialog.AbstractCRUDDialogController;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;
import com.synaptix.widget.view.dialog.ICRUDBeanDialogView;

public class StatusGraphDialogController extends AbstractCRUDDialogController<IStatusGraph> {

	private final ITaskManagerViewFactory viewFactory;

	private final ITaskManagerController taskManagerController;

	private final Class<? extends ITaskObject<?>> taskObjectClass;

	private ICRUDBeanDialogView<IStatusGraph> beanDialogView;

	public StatusGraphDialogController(ITaskManagerViewFactory viewFactory, ITaskManagerController taskManagerController, Class<? extends ITaskObject<?>> taskObjectClass) {
		super(IStatusGraph.class, StaticHelper.getTaskManagerConstantsBundle().statusGraph(), StaticHelper.getTaskManagerConstantsBundle().addStatusGraph(), StaticHelper
				.getTaskManagerConstantsBundle().editStatusGraph());
		this.viewFactory = viewFactory;
		this.taskManagerController = taskManagerController;
		this.taskObjectClass = taskObjectClass;
		initialize();
	}

	@SuppressWarnings("unchecked")
	private void initialize() {
		IBeanExtensionDialogView<IStatusGraph> generalStatusGraphExtensionBeanDialogView = viewFactory.newGeneralStatusGraphBeanExtensionDialogView(taskManagerController, taskObjectClass);
		beanDialogView = viewFactory.newCRUDBeanDialogView(generalStatusGraphExtensionBeanDialogView);
	}

	@Override
	protected ICRUDBeanDialogView<IStatusGraph> getCRUDBeanDialogView() {
		return beanDialogView;
	}
}
