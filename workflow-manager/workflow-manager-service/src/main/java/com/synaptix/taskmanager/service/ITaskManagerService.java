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
	 * Starts task manager engine on task object in parameter. If the object is not already linked to a cluster, creates the task cluster.
	 */
	public <E extends Enum<E>, F extends ITaskObject<E>> IServiceResult<Void> startEngine(F taskObject);

	/**
	 * Starts the task manager engine. Links the object to a new cluster if the object doesn't already have one.
	 * 
	 * @param idTaskObject
	 *            ID of the task object.
	 * @param objectClass
	 */
	public <E extends Enum<E>, F extends ITaskObject<E>> IServiceResult<Void> startEngine(IId idTaskObject, Class<F> objectClass);

	/**
	 * Start engine with cluster
	 *
	 * @param idTaskCluster
	 */
	public IServiceResult<Void> startEngine(IId idTaskCluster);

	<E extends Enum<E>, F extends ITaskObject<E>> IServiceResult<Void> startEngine(List<IId> idTaskObjects, Class<F> objectClass);

	/**
	 * Add task object to task cluster same taskObjectWithCluster, no start engine
	 * 
	 * @param taskObjectWithCluster
	 * @param newTaskObject
	 */
	public <E extends Enum<E>, F extends ITaskObject<E>, G extends Enum<G>, H extends ITaskObject<G>> void addTaskObjectToTaskCluster(F taskObjectWithCluster, H newTaskObject);

	/**
	 * Add task object in task cluster, no start engine
	 * 
	 * @param idTaskCluster
	 * @param taskObject
	 */
	public abstract  <E extends Enum<E>, F extends ITaskObject<E>> void addTaskObjectToTaskCluster(IId idTaskCluster, F taskObject);

	/**
	 * Skip a task if skippable
	 * 
	 * @param idTask
	 * @param skipComments
	 * @return true if skipped
	 */
	public boolean skipTask(IId idTask, String skipComments);

	/**
	 * Update description of the object in the todo (code field).
	 */
	public void updateTodoDescription(ITodo todo);

	/**
	 * Update description of the object.
	 */
	public void updateTodoDescription(IId idObject, Class<? extends ITaskObject<?>> objectClass);

	/**
	 * Set check_user_validation to true.
	 */
	public void validateTask(IId idTask);

	<E extends Enum<E>, F extends ITaskObject<E>> void cancelTaskObject(F object);

	IServiceResult<Void> restart();

	<E extends Enum<E>, F extends ITaskObject<E>> void addToQueue(F taskObject);

	public void saveErrors(ITask task, Set<IError> errors);

	public ITask selectCurrentTaskByIdObject(IId idObject);

	/**
	 * Returns the shortest path between two statuses.
	 *
	 * @param currentStatus start status
	 * @param nextStatus target status
	 * @return Empty String if no path was found
	 */
	public String getStatusPath(Class<? extends ITaskObject<?>> taskObjectClass, String currentStatus, String nextStatus);

	/**
	 * Delete an archived task cluster and all tasks that were linked to this cluster.
	 */
	void deleteTasksCluster(IId idCluster);
}
