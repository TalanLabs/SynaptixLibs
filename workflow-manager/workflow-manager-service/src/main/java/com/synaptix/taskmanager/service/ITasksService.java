package com.synaptix.taskmanager.service;

import java.io.Serializable;
import java.util.List;

import com.synaptix.taskmanager.model.IAssoTaskPreviousTask;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;

public interface ITasksService {

	public List<ITask> findTasksBy(Class<? extends ITaskObject<?>> taskObjectClass, Serializable idObject);

	public List<IAssoTaskPreviousTask> findAssoTaskPreviousTasksBy(Class<? extends ITaskObject<?>> taskObjectClass, Serializable idObject);

	List<ITask> findTasksByCluster(Serializable idCluster);

	List<IAssoTaskPreviousTask> findAssoTaskPreviousTasksByCluster(Serializable idCluster);

}
