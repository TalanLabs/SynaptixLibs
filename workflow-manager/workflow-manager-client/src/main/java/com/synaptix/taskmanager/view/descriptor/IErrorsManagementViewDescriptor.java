package com.synaptix.taskmanager.view.descriptor;

import com.synaptix.entity.IErrorEntity;
import com.synaptix.entity.IId;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.widget.component.view.IComponentsManagementViewDescriptor;

public interface IErrorsManagementViewDescriptor extends IComponentsManagementViewDescriptor<IErrorEntity> {

	/**
	 * Filters the errors corresponding to a taskObject.
	 * 
	 * @param taskObject
	 */
	public <E extends ITaskObject<?>> void searchByObject(E taskObject);

	/**
	 * Filters the errors for a task.
	 * 
	 * @param idTask
	 *            ID of the task.
	 */
	public void searchByTask(IId idTask);

}
