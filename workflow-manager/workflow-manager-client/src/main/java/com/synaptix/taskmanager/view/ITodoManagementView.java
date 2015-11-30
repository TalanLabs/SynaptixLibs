package com.synaptix.taskmanager.view;

import java.util.List;

import com.synaptix.client.view.IView;
import com.synaptix.entity.IId;
import com.synaptix.taskmanager.model.ITodo;
import com.synaptix.taskmanager.model.ITodoFolderListView;

public interface ITodoManagementView extends IView {

	void updateDetailsPanel();

	void updateTodos(List<ITodo> todoList, List<IId> idObjects);

	void setTodos(List<ITodo> todoList);

	void setTodoFoldersList(List<ITodoFolderListView> folderList, int total, boolean reloadSelectedFolder);

	boolean isLastTodosFirst();

	String getSearchText();

}
