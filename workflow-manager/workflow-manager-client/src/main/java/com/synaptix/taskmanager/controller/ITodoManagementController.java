package com.synaptix.taskmanager.controller;

import java.util.List;

import com.synaptix.entity.IId;

public interface ITodoManagementController {

	void reloadTodoDetails();

	void loadTodoFoldersList();

	void refresh(List<IId> idObjects);
}
