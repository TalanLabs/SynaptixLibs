package com.synaptix.taskmanager.service;

import java.io.Serializable;
import java.util.List;

import com.google.inject.Inject;
import com.synaptix.mybatis.delegate.EntityServiceDelegate;
import com.synaptix.service.ServiceException;
import com.synaptix.taskmanager.dao.mapper.AssoTaskPreviousTaskMapper;
import com.synaptix.taskmanager.dao.mapper.TaskMapper;
import com.synaptix.taskmanager.model.IAssoTaskPreviousTask;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskCluster;
import com.synaptix.taskmanager.model.ITaskObject;

public class TasksServerService extends AbstractSimpleService implements ITasksService {

	@Inject
	private EntityServiceDelegate entityServiceDelegate;

	private final AssoTaskPreviousTaskMapper getAssoTaskPreviousTaskMapper() {
		return getDaoSession().getMapper(AssoTaskPreviousTaskMapper.class);
	}

	private final TaskMapper getTaskMapper() {
		return getDaoSession().getMapper(TaskMapper.class);
	}

	@Override
	public List<ITask> findTasksBy(Class<? extends ITaskObject<?>> taskObjectClass, Serializable idObject) {
		try {
			getDaoSession().begin();
			return getTaskMapper().findTasksBy(taskObjectClass, idObject);
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			getDaoSession().end();
		}
	}

	@Override
	public List<ITask> findTasksByCluster(Serializable idCluster) {
		try {
			getDaoSession().begin();

			ITaskCluster taskCluster = entityServiceDelegate.findEntityById(ITaskCluster.class, idCluster);
			if (taskCluster.isCheckArchive()) {
				return getTaskMapper().findTaskArchsByCluster(idCluster);
			} else {
				return getTaskMapper().findTasksByCluster(idCluster);
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			getDaoSession().end();
		}
	}

	@Override
	public List<IAssoTaskPreviousTask> findAssoTaskPreviousTasksBy(Class<? extends ITaskObject<?>> taskObjectClass, Serializable idObject) {
		try {
			getDaoSession().begin();
			return getAssoTaskPreviousTaskMapper().selectAssoTaskPreviousTasksBy(taskObjectClass, idObject);
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			getDaoSession().end();
		}
	}

	@Override
	public List<IAssoTaskPreviousTask> findAssoTaskPreviousTasksByCluster(Serializable idCluster) {
		try {
			getDaoSession().begin();

			ITaskCluster taskCluster = entityServiceDelegate.findEntityById(ITaskCluster.class, idCluster);
			if (taskCluster.isCheckArchive()) {
				return getAssoTaskPreviousTaskMapper().selectAssoTaskPreviousTaskArchsByCluster(idCluster);
			} else {
				return getAssoTaskPreviousTaskMapper().selectAssoTaskPreviousTasksByCluster(idCluster);
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			getDaoSession().end();
		}
	}

}
