package com.synaptix.taskmanager.service;

import java.util.List;

import com.synaptix.entity.IId;
import com.synaptix.taskmanager.model.IAssoTaskPreviousTask;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;

public interface ITasksService {

	public List<ITask> findTasksBy(Class<? extends ITaskObject<?>> taskObjectClass, IId idObject);

	public List<IAssoTaskPreviousTask> findAssoTaskPreviousTasksBy(Class<? extends ITaskObject<?>> taskObjectClass, IId idObject);

	List<ITask> findTasksByCluster(IId idCluster);

	List<IAssoTaskPreviousTask> findAssoTaskPreviousTasksByCluster(IId idCluster);

}
