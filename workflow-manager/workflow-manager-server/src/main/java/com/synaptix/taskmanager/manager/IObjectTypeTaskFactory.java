package com.synaptix.taskmanager.manager;

import java.io.Serializable;

import com.synaptix.entity.IEntity;
import com.synaptix.taskmanager.manager.taskservice.ITaskService;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskChainCriteria;
import com.synaptix.taskmanager.model.ITaskObject;

public interface IObjectTypeTaskFactory<F extends ITaskObject<?>> {

	Class<F> getTaskObjectClass();

	/**
	 * Get description of the TaskObject
	 */
	String getTaskObjectDescription(Serializable idObject);

	/**
	 * Get a task chain criteria for group task
	 */
	ITaskChainCriteria<? extends Enum<?>> getTaskChainCriteria(ITask task);

	/**
	 * Get a task chain criteria from context
	 */
	ITaskChainCriteria<? extends Enum<?>> getTaskChainCriteria(Object context, String status, String nextStatus);

	/**
	 * Get a executant for the task
	 */
	IEntity getExecutant(ITask task);

	/**
	 * Get a manager for the task
	 */
	IEntity getManager(ITask task);

	/**
	 * Get a default todo descriptor
	 */
	ITaskService.ITodoDescriptor getDefaultTodoDescriptor();

}
