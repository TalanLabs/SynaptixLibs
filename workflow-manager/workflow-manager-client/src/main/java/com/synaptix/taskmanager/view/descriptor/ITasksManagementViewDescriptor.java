package com.synaptix.taskmanager.view.descriptor;

import java.io.Serializable;

import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.widget.component.view.IComponentsManagementViewDescriptor;

public interface ITasksManagementViewDescriptor extends IComponentsManagementViewDescriptor<ITask> {

	/**
	 * Search a task object
	 * 
	 * @param taskObject
	 */
	public <E extends ITaskObject<?>> void searchBy(E taskObject);

	public void searchByCluster(Serializable idCluster);

}
