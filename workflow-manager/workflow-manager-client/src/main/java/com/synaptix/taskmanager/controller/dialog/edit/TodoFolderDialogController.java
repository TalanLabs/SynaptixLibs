package com.synaptix.taskmanager.controller.dialog.edit;

import com.synaptix.taskmanager.model.ITodoFolder;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.widget.component.controller.dialog.AbstractCRUDDialogController;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;
import com.synaptix.widget.view.dialog.ICRUDBeanDialogView;

public class TodoFolderDialogController extends AbstractCRUDDialogController<ITodoFolder> {

	private ICRUDBeanDialogView<ITodoFolder> TodoFolderBeanDialogView;

	private ITaskManagerViewFactory viewFactory;

	public TodoFolderDialogController(ITaskManagerViewFactory viewFactory) {
		super(ITodoFolder.class, StaticHelper.getTaskManagerConstantsBundle().todoFolder(), StaticHelper.getTaskManagerConstantsBundle().addTodoFolder(), StaticHelper.getTaskManagerConstantsBundle()
				.edit());
		this.viewFactory = viewFactory;
		initialize();
	}

	@SuppressWarnings("unchecked")
	private void initialize() {
		IBeanExtensionDialogView<ITodoFolder> generalTodoFolderExtensionBeanDialogView = viewFactory.newGeneralTodoFolderExtensionBeanDialogView();
		TodoFolderBeanDialogView = viewFactory.newCRUDBeanDialogView(generalTodoFolderExtensionBeanDialogView);
	}

	@Override
	protected ICRUDBeanDialogView<ITodoFolder> getCRUDBeanDialogView() {
		return TodoFolderBeanDialogView;
	}
}
