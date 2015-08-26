package com.synaptix.taskmanager.dao.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.joda.time.LocalDateTime;

import com.synaptix.taskmanager.model.IClusterTaskResult;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;


public interface TaskMapper {

	/**
	 * Compte les taches qui ont comme parent la tache en paramètre et qui sont en todo ou current
	 * 
	 * @param idParentTask
	 * @return
	 */
	public int countTodoCurrentSubTask(@Param("idParentTask") Serializable idParentTask);

	/**
	 * Trouve toutes les taches en current du cluster
	 * 
	 * @param idTaskCluster
	 * @return
	 */
	public List<ITask> selectCurrentTasksForCluster(@Param("idTaskCluster") Serializable idTaskCluster);

	/**
	 * Trouve toutes les taches todo qui sont suivantes de la tache en parametre et qui ont tous les autres précedents de fait.
	 * 
	 * @param idPreviousTask
	 * @return
	 */
	public List<ITask> selectNextTodoToCurrentTasks(@Param("idPreviousTask") Serializable idPreviousTask);

	/**
	 * Trouve toutes les taches todo ou current suivantes de la tâche en parametre
	 * 
	 * @param idPreviousTask
	 * @return
	 */
	public List<ITask> selectNextTasks(@Param("idPreviousTask") Serializable idPreviousTask, @Param("idIgnoreTask") Serializable idIgnoreTask);

	/**
	 * Trouve toutes les taches todo ou current précedente de la tâche en parametre
	 * 
	 * @param idTask
	 * @return
	 */
	public List<ITask> selectPreviousTodoCurrentTasks(@Param("idTask") Serializable idTask);

	/**
	 * Trouve toutes les taches todo ou current qui sont le fils de la tache en parametre
	 * 
	 * @param idParentTask
	 * @return
	 */
	public List<ITask> selectTodoCurrentSubTasks(@Param("idParentTask") Serializable idParentTask);

	/**
	 * Trouve toutes les tâches current qui doivent avoir peut-etre un todo manager
	 * 
	 * @param currentDate
	 * @return
	 */
	public List<IClusterTaskResult> selectTodoManagerTasks(@Param("currentDate") LocalDateTime currentDate);

	/**
	 * Find the list of tasks that has the task in parameter as previous task.
	 */
	public List<Serializable> findNextTasks(@Param("idTask") Serializable idTask);

	/**
	 * Find the tasks that (directly) precede the task in parameter.
	 */
	public List<Serializable> findPreviousTasks(@Param("idTask") Serializable idTask);

	/**
	 * Delete a task.
	 */
	public void deleteTask(@Param("idTask") Serializable idTask);

	public void deleteTasks(@Param("idList") List<Serializable> idTaskList);

	/**
	 * Finds IDs of task to delete when a graph branch is completed. Selects all the tasks after the previousUpdateTask which are not in the finished branch (the one going to nextStatus).
	 * 
	 * @param idPreviousUpdateTask
	 *            ID of the previous update task.
	 * @param nextStatus
	 *            Status the object has been updated to.
	 * @return List of IDs
	 */
	public List<Serializable> findTasksToDelete(@Param("idPreviousUpdateTask") Serializable idPreviousUpdateTask, @Param("nextStatus") String nextStatus);

	public List<ITask> findTasksBy(@Param("taskObjectClass") Class<? extends ITaskObject<?>> taskObjectClass, @Param("idObject") Serializable idObject);

	public List<ITask> findTasksByCluster(@Param("idCluster") Serializable idCluster);

	public List<ITask> findTaskArchsByCluster(@Param("idCluster") Serializable idCluster);

	public void updateIdCluster(@Param("newIdCluster") Serializable idTaskCluster, @Param("idObject") Serializable idObject);

	public void deleteCluster(@Param("id") Serializable id);

	public LocalDateTime getTaskEndTime(@Param("idTaskObject") Serializable idTaskObject, @Param("taskCode") String taskCode);

	void validateTask(@Param("idTask") Serializable idTask);

	void deleteObjectTasks(@Param("idObject") Serializable idObject);

	void deleteObjectTaskAssos(@Param("idObject") Serializable idObject);

	void removeObjectFromCluster(@Param("idObject") Serializable idObject);

	ITask selectCurrentTaskByIdObject(@Param("idObject") Serializable idObject);

}