package com.synaptix.taskmanager.service;

import java.util.List;
import java.util.Set;

import com.synaptix.component.model.IError;
import com.synaptix.component.model.IServiceResult;
import com.synaptix.entity.IId;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.ITodo;

public interface ITaskManagerService {

	/**
	 * Starts task manager engine on task object in parameter. If the object is not already linked to a cluster, creates the task cluster.<br/>
	 * Raised errors in the set comes from the business part. The service result contains technical errors.
	 */
	<E extends Enum<E>, F extends ITaskObject<E>> IServiceResult<Set<IError>> startEngine(F taskObject);

	/**
	 * Starts the task manager engine. Links the object to a new cluster if the object doesn't already have one.<br/>
	 * Raised errors in the set comes from the business part. The service result contains technical errors.
	 *
	 * @param idTaskObject
	 *            ID of the task object.
	 */
	<E extends Enum<E>, F extends ITaskObject<E>> IServiceResult<Set<IError>> startEngine(IId idTaskObject, Class<F> objectClass);

	/**
	 * Start engine with cluster<br/>
	 * Raised errors in the set comes from the business part. The service result contains technical errors.
	 */
	IServiceResult<Set<IError>> startEngine(IId idTaskCluster);

	/**
	 * Raised errors in the set comes from the business part. The service result contains technical errors.
	 */
	<E extends Enum<E>, F extends ITaskObject<E>> IServiceResult<Set<IError>> startEngine(List<IId> idTaskObjects, Class<F> objectClass);

	/**
	 * Add task object to task cluster same taskObjectWithCluster, no start engine
	 */
	<E extends Enum<E>, F extends ITaskObject<E>, G extends Enum<G>, H extends ITaskObject<G>> void addTaskObjectToTaskCluster(F taskObjectWithCluster, H newTaskObject);

	/**
	 * Add task object in task cluster, no start engine
	 */
	<E extends Enum<E>, F extends ITaskObject<E>> void addTaskObjectToTaskCluster(IId idTaskCluster, F taskObject);

	/**
	 * Skip a task if skippable
	 *
	 * @return true if skipped
	 */
	boolean skipTask(IId idTask, String skipComments);

	/**
	 * Update description of the object in the todo (code field).
	 */
	void updateTodoDescription(ITodo todo);

	/**
	 * Update description of the object.
	 */
	void updateTodoDescription(IId idObject, Class<? extends ITaskObject<?>> objectClass);

	/**
	 * Set check_user_validation to true.
	 */
	void validateTask(IId idTask);

	<E extends Enum<E>, F extends ITaskObject<E>> void cancelTaskObject(F object);

	/**
	 * Raised errors in the set comes from the business part. The service result contains technical errors.
	 */
	IServiceResult<Set<IError>> restart();

	/**
	 * Clears the task manager queue
	 */
	void clear();

	<E extends Enum<E>, F extends ITaskObject<E>> void addToQueue(F taskObject);

	void saveErrors(ITask task, Set<IError> errors);

	ITask selectCurrentTaskByIdObject(IId idObject);

	/**
	 * Returns the shortest path between two statuses.
	 *
	 * @param currentStatus
	 *            start status
	 * @param nextStatus
	 *            target status
	 * @return Empty String if no path was found
	 */
	String getStatusPath(Class<? extends ITaskObject<?>> taskObjectClass, String currentStatus, String nextStatus);

	/**
	 * Delete an archived task cluster and all tasks that were linked to this cluster.
	 */
	void deleteTasksCluster(IId idCluster);
}
