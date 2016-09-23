package com.synaptix.taskmanager.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.joda.time.LocalDateTime;

import com.synaptix.entity.IId;
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
	public int countTodoCurrentSubTask(@Param("idParentTask") IId idParentTask);

	/**
	 * Trouve toutes les taches en current du cluster
	 * 
	 * @param idTaskCluster
	 * @return
	 */
	public List<ITask> selectCurrentTasksForCluster(@Param("idTaskCluster") IId idTaskCluster);

	/**
	 * Trouve toutes les taches todo qui sont suivantes de la tache en parametre et qui ont tous les autres précedents de fait.
	 * 
	 * @param idPreviousTask
	 * @return
	 */
	public List<ITask> selectNextTodoToCurrentTasks(@Param("idPreviousTask") IId idPreviousTask);

	/**
	 * Trouve toutes les taches todo ou current suivantes de la tâche en parametre
	 * 
	 * @param idPreviousTask
	 * @return
	 */
	public List<ITask> selectNextTasks(@Param("idPreviousTask") IId idPreviousTask, @Param("idIgnoreTask") IId idIgnoreTask);

	/**
	 * Trouve toutes les taches todo ou current précedente de la tâche en parametre
	 * 
	 * @param idTask
	 * @return
	 */
	public List<ITask> selectPreviousTodoCurrentTasks(@Param("idTask") IId idTask);

	/**
	 * Trouve toutes les taches todo ou current qui sont le fils de la tache en parametre
	 * 
	 * @param idParentTask
	 * @return
	 */
	public List<ITask> selectTodoCurrentSubTasks(@Param("idParentTask") IId idParentTask);

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
	public List<IId> findNextTasks(@Param("idTask") IId idTask);

	/**
	 * Find the tasks that (directly) precede the task in parameter.
	 */
	public List<IId> findPreviousTasks(@Param("idTask") IId idTask);

	/**
	 * Delete a task.
	 */
	public void deleteTask(@Param("idTask") IId idTask);

	public void deleteTasks(@Param("idList") List<IId> idTaskList);

	/**
	 * Finds IDs of task to delete when a graph branch is completed. Selects all the tasks after the previousUpdateTask which are not in the finished branch (the one going to nextStatus).
	 * 
	 * @param idPreviousUpdateTask
	 *            ID of the previous update task.
	 * @param nextStatus
	 *            Status the object has been updated to.
	 * @return List of IDs
	 */
	public List<IId> findTasksToDelete(@Param("idPreviousUpdateTask") IId idPreviousUpdateTask, @Param("nextStatus") String nextStatus);

	public List<ITask> findTasksBy(@Param("taskObjectClass") Class<? extends ITaskObject<?>> taskObjectClass, @Param("idObject") IId idObject);

	public List<ITask> findTasksByCluster(@Param("idCluster") IId idCluster);

	public List<ITask> findTaskArchsByCluster(@Param("idCluster") IId idCluster);

	public void updateIdCluster(@Param("newIdCluster") IId idTaskCluster, @Param("idObject") IId idObject);

	public void deleteCluster(@Param("id") IId id);

	public LocalDateTime getTaskEndTime(@Param("idTaskObject") IId idTaskObject, @Param("taskCode") String taskCode);

	void validateTask(@Param("idTask") IId idTask);

	void deleteObjectTasks(@Param("idObject") IId idObject);

	void deleteObjectTaskAssos(@Param("idObject") IId idObject);

	void removeObjectFromCluster(@Param("idObject") IId idObject);

	ITask selectCurrentTaskByIdObject(@Param("idObject") IId idObject);

	boolean hasCurrentTasks(@Param("idTaskCluster") IId idTaskCluster);
}
