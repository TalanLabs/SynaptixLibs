package com.synaptix.taskmanager.controller;

import java.io.Serializable;
import java.util.List;

public interface ITodoManagementController {

	void reloadTodoDetails();

	void loadTodoFoldersList();

	void refresh(List<Serializable> idObjects);
}
