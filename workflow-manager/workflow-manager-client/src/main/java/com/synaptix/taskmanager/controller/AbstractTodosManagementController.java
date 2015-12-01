package com.synaptix.taskmanager.controller;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;

import org.apache.commons.collections4.CollectionUtils;

import com.synaptix.client.view.IView;
import com.synaptix.client.view.IWaitWorker;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.entity.IId;
import com.synaptix.service.IServiceFactory;
import com.synaptix.taskmanager.model.ITodo;
import com.synaptix.taskmanager.model.ITodoFolderListView;
import com.synaptix.taskmanager.service.ITodoService;
import com.synaptix.taskmanager.urimanager.IURIClientManagerDiscovery;
import com.synaptix.taskmanager.urimanager.uriaction.ITodoAction;
import com.synaptix.taskmanager.urimanager.uriaction.IURIClientManager;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.taskmanager.view.ITodoManagementView;
import com.synaptix.widget.core.controller.AbstractController;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.viewworker.view.AbstractLoadingViewWorker;

public abstract class AbstractTodosManagementController extends AbstractController implements ITodoManagementController {

	private final ITaskManagerViewFactory taskManagerViewFactory;

	private final IURIClientManagerDiscovery uriActionDiscovery;

	private final ITodoManagementView todoManagementView;

	private final int sizePage;

	private IWaitWorker loadComponentsWorker;

	private ITodoFolderListView selectedTodoFolder;

	public AbstractTodosManagementController(ITaskManagerViewFactory taskManagerViewFactory, IURIClientManagerDiscovery uriActionDiscovery) {
		super();
		this.taskManagerViewFactory = taskManagerViewFactory;
		this.uriActionDiscovery = uriActionDiscovery;

		this.todoManagementView = taskManagerViewFactory.createTodoPanel(this);

		this.sizePage = 15;
	}

	private ITaskManagerViewFactory getViewFactory() {
		return this.taskManagerViewFactory;
	}

	@Override
	public IView getView() {
		return todoManagementView;
	}

	public void loadTodoFoldersList() {
		getViewFactory().waitFullComponentViewWorker(getView(), new AbstractLoadingViewWorker<TodoFolderResult>() {

			@Override
			protected TodoFolderResult doLoading() throws Exception {
				TodoFolderResult result = new TodoFolderResult();
				result.folderList = findTodoFolders();
				result.total = countAllTodos();
				return result;
			}

			@Override
			public void success(TodoFolderResult result) {
				todoManagementView.setTodoFoldersList(result.folderList, result.total, true);
				todoManagementView.setTodos(null); // TODO "voir tout"?
			}

			@Override
			public void fail(Throwable t) {
				getViewFactory().showErrorMessageDialog(getView(), t);
			}
		});
	}

	/**
	 * Return list of folders that should be displayed to the current user. This method will be called inside a worker thread.
	 */
	public abstract List<ITodoFolderListView> findTodoFolders();

	public abstract int countAllTodos();

	public List<ITodoAction> getUriTodoActions(final List<ITodo> list) {
		if (CollectionUtils.isEmpty(list)) {
			return Collections.emptyList();
		}
		List<ITodoAction> todoReturnedActions = new ArrayList<ITodoAction>();
		List<ITodoAction> toRemove = new ArrayList<ITodoAction>();
		boolean firstLoop = true;
		for (ITodo todo : list) {
			IURIClientManager uriAction = uriActionDiscovery.getURIClientManager(todo.getUri());
			if (uriAction != null) {
				List<ITodoAction> actionsToAdd = uriAction.getActions(todo);
				if (todoReturnedActions.isEmpty() && firstLoop) {
					todoReturnedActions.addAll(actionsToAdd);
					firstLoop = false;
				} else {
					for (ITodoAction todoActionReturned : todoReturnedActions) {
						boolean remove = true;
						if (todoActionReturned.isMultiSelectionOk()) {
							for (ITodoAction todoAction : actionsToAdd) {
								if (todoAction.isMultiSelectionOk() && todoActionReturned.getClass().equals(todoAction.getClass())) {
									remove = false;
								}
							}
						}
						if (remove) {
							toRemove.add(todoActionReturned);
						}
					}
				}
			}
		}
		todoReturnedActions.removeAll(toRemove);
		return todoReturnedActions;
	}

	public void getUriDetailsPanel(final ITodo todo, IView view, IResultCallback<Component> callback) {
		if (todo == null) {
			return;
		}
		IURIClientManager uriAction = uriActionDiscovery.getURIClientManager(todo.getUri());
		if (uriAction != null) {
			uriAction.getDetailsPanel(todo, view, callback);
		}
	}

	protected abstract IServiceFactory getServiceFactory();

	/**
	 * Reload todos for the objects in parameter.
	 */
	public void refresh(final List<IId> idObjects) {
		getViewFactory().waitFullComponentViewWorker(getView(), new AbstractLoadingViewWorker<TodoFolderResult>() {

			@Override
			protected TodoFolderResult doLoading() throws Exception {
				TodoFolderResult result = new TodoFolderResult();
				result.folderList = findTodoFolders();
				result.total = countAllTodos();
				result.todoList = selectTodoListForObjects(selectedTodoFolder, idObjects);
				return result;
			}

			@Override
			public void success(TodoFolderResult result) {
				todoManagementView.setTodoFoldersList(result.folderList, result.total, false);
				todoManagementView.updateTodos(result.todoList, idObjects); // TODO "voir tout"?
			}

			@Override
			public void fail(Throwable t) {
				getViewFactory().showErrorMessageDialog(getView(), t);
			}
		});
	}

	protected abstract List<ITodo> selectTodoListForObjects(ITodoFolderListView selectedTodoFolder, List<IId> idObjects);

	public void reloadTodoDetails() {
		todoManagementView.updateDetailsPanel();
	}

	public void reloadPage() {
		updatePagination(selectedTodoFolder);
	}

	/**
	 * Cancel loading
	 */
	public final void cancelWork() {
		if (loadComponentsWorker != null && !loadComponentsWorker.isDone()) {
			loadComponentsWorker.cancel(false);
			loadComponentsWorker = null;
		}
	}

	/**
	 * Reset the statistics + table
	 */
	public final void reset() {
		todoManagementView.setTodos(null);
	}

	public void setSelectedTodoFolder(ITodoFolderListView todoFolder) {
		reset();

		this.selectedTodoFolder = todoFolder;
		updatePagination(selectedTodoFolder);
	}

	private void updatePagination(final ITodoFolderListView selectedTodoFolder) {
		cancelWork();
		loadComponentsWorker = getViewFactory().waitFullComponentViewWorker(getView(), new AbstractLoadingViewWorker<List<ITodo>>() {

			@Override
			protected List<ITodo> doLoading() throws Exception {
				publish(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().fetchingXLines(sizePage));
				return findTodosForFolder(selectedTodoFolder);
			}

			@Override
			public void success(List<ITodo> e) {
				todoManagementView.setTodos(e);
			}

			@Override
			public void fail(Throwable t) {
				if (!(t instanceof CancellationException)) {
					getViewFactory().showErrorMessageDialog(getView(), t);

					reset();
				}
			}
		});
	}

	protected abstract List<ITodo> findTodosForFolder(ITodoFolderListView selectedTodoFolder);

	protected String getSearchText() {
		return todoManagementView.getSearchText();
	}

	protected boolean isLastTodosFirst() {
		return todoManagementView.isLastTodosFirst();
	}

	private ITodoService getTodoService() {
		return getServiceFactory().getService(ITodoService.class);
	}

	private final class TodoFolderResult {
		public List<ITodo> todoList;
		List<ITodoFolderListView> folderList;
		int total;
	}
}
