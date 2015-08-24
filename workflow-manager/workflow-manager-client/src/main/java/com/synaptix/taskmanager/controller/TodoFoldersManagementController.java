package com.synaptix.taskmanager.controller;

import java.io.Serializable;

import com.synaptix.common.util.IResultCallback;
import com.synaptix.taskmanager.controller.dialog.edit.TodoFolderDialogController;
import com.synaptix.client.common.controller.AbstractPscSimpleNlsCRUDManagementController;
import com.synaptix.taskmanager.helper.StaticTaskManagerHelper;
import com.synaptix.taskmanager.model.ITodoFolder;
import com.synaptix.taskmanager.service.ITodoFolderService;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.widget.component.controller.dialog.ICRUDDialogController;
import com.synaptix.widget.crud.view.descriptor.ICRUDManagementViewDescriptor;

public class TodoFoldersManagementController extends AbstractPscSimpleNlsCRUDManagementController<ITaskManagerViewFactory, ITodoFolder> {

	public TodoFoldersManagementController(ITaskManagerViewFactory taskManagerViewFactory) {
		super(taskManagerViewFactory, ITodoFolder.class, ITodoFolderService.class, null);
	}

	@Override
	public boolean hasAuthWrite() {
		return StaticTaskManagerHelper.getTaskManagerAuthsBundle().hasWriteTodoFoldersManagement();
	}

	@Override
	protected ICRUDDialogController<ITodoFolder> newCRUDDialogController(DialogAction dialogAction, ITodoFolder entity) {
		return new TodoFolderDialogController(getViewFactory());
	}

	@Override
	protected ICRUDManagementViewDescriptor<ITodoFolder> createCRUDManagementViewDescriptor() {
		return getViewFactory().createTodoFolderViewDescriptor(this);
	}

	@Override
	protected void addEntitySuccess(final Serializable idTodoFolder) {
		operationEntity(idTodoFolder);
	}

	@Override
	protected void editEntitySuccess(final Serializable idTodoFolder) {
		operationEntity(idTodoFolder);
	}

	private void operationEntity(final Serializable idTodoFolder) {
		if (idTodoFolder == null) {
			_operationEntitySuccess(idTodoFolder);
		} else {
			final Callback callback = new Callback() {
				@Override
				public void callback() {
					_operationEntitySuccess(idTodoFolder);
				}
			};
			loadEntity(idTodoFolder, new IResultCallback<ITodoFolder>() {

				@Override
				public void setResult(ITodoFolder e) {
//					if ((e == null) || (!e.isRestricted())) {
//						saveRoles(idTodoFolder, null, callback);
//					} else {
//						editRoles(idTodoFolder, callback);
//					}
				}
			});
		}
	}

	private void _operationEntitySuccess(final Serializable idTodoFolder) {
		loadPagination();
	}

//	public void editRoles(final Serializable idTodoFolder) {
//		editRoles(idTodoFolder, null);
//	}

//	private void editRoles(final Serializable idTodoFolder, final Callback callback) {
//		getViewFactory().waitFullComponentViewWorker(getView(), new AbstractLoadingViewWorker<Pair<List<IRole>, List<IRole>>>() {
//
//			@Override
//			protected Pair<List<IRole>, List<IRole>> doLoading() throws Exception {
//				return getAssoTodoFolderRoleService().loadRoles(idTodoFolder);
//			}
//
//			@Override
//			public void success(Pair<List<IRole>, List<IRole>> e) {
//				IAssoTodoFolderRoleDialogView assoView = getViewFactory().createAssoTodoFolderRole();
//				if (assoView.showDialog(getView(), e.getLeft(), e.getRight()) == AbstractSimpleDialog2.ACCEPT_OPTION) {
//					saveRoles(idTodoFolder, assoView.getSelectedRoles(), callback);
//				} else if (callback != null) {
//					callback.callback();
//				}
//			}
//
//			@Override
//			public void fail(Throwable t) {
//				getViewFactory().showErrorMessageDialog(getView(), t);
//			}
//		});
//	}

//	private void saveRoles(final Serializable idTodoFolder, final List<IRole> selectedRoles, final Callback callback) {
//		getViewFactory().waitFullComponentViewWorker(getView(), new AbstractSavingViewWorker<Void>() {
//
//			@Override
//			protected Void doSaving() throws Exception {
//				List<Serializable> roleList = new ArrayList<Serializable>();
//				if (CollectionHelper.isNotEmpty(selectedRoles)) {
//					for (IRole role : selectedRoles) {
//						roleList.add(role.getId());
//					}
//				}
//				getAssoTodoFolderRoleService().saveRoles(idTodoFolder, roleList);
//				return null;
//			}
//
//			@Override
//			public void success(Void e) {
//				if (callback != null) {
//					callback.callback();
//				} else {
//					loadPagination();
//				}
//			}
//
//			@Override
//			public void fail(Throwable t) {
//				getViewFactory().showErrorMessageDialog(getView(), t);
//			}
//		});
//	}

//	private IAssoTodoFolderRoleService getAssoTodoFolderRoleService() {
//		return getServiceFactory().getService(IAssoTodoFolderRoleService.class);
//	}

	private interface Callback {

		public void callback();

	}
}
