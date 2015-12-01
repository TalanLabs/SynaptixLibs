package com.synaptix.taskmanager.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.synaptix.entity.IId;
import com.synaptix.taskmanager.model.IAssoTaskPreviousTask;
import com.synaptix.taskmanager.model.ITaskObject;

public interface AssoTaskPreviousTaskMapper {

	/**
	 * Insert asso task previous task
	 * 
	 * @param build
	 */
	public void insertAssoTaskPreviousTask(IAssoTaskPreviousTask assoTaskPreviousTask);

	/**
	 * Trouve toutes les asso selon le task object class et l'id object
	 * 
	 * @param taskObjectClass
	 * @param idObject
	 * @return
	 */
	public List<IAssoTaskPreviousTask> selectAssoTaskPreviousTasksBy(@Param("taskObjectClass") Class<? extends ITaskObject<?>> taskObjectClass, @Param("idObject") IId idObject);

	/**
	 * Selects tasks associations for a cluster.
	 */
	public List<IAssoTaskPreviousTask> selectAssoTaskPreviousTasksByCluster(@Param("idCluster") IId idCluster);

	/**
	 * Selects tasks associations for a cluster archive
	 * 
	 * @param idCluster
	 * @return
	 */
	public List<IAssoTaskPreviousTask> selectAssoTaskPreviousTaskArchsByCluster(@Param("idCluster") IId idCluster);

	public void deleteTaskAsso(@Param("idTask") IId idTask);

	public void deleteTasksAssos(@Param("idList") List<IId> idTaskList);
}
