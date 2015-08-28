package com.synaptix.taskmanager.manager.taskservice;

import java.io.Serializable;

import com.google.inject.Inject;
import com.synaptix.mybatis.dao.IDaoSession;
import com.synaptix.service.ServiceException;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.domains.ServiceNature;

public abstract class AbstractCheckNextStatusTaskService extends AbstractTaskService<Serializable> {

	@Inject
	private IDaoSession daoSession;

	public AbstractCheckNextStatusTaskService(String code, Class<? extends ITaskObject<?>> objectType) {
		super(code, ServiceNature.DATA_CHECK, objectType);
	}

	@Override
	public final IExecutionResult execute(ITask task) {
		IExecutionResult executionResult;
		try {
			daoSession.begin();
			executionResult = execute(task.getIdObject());
			daoSession.commit();
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			daoSession.end();
		}
		return executionResult;
	}

	protected abstract IExecutionResult execute(Serializable idObject);

	@Override
	public IExecutionResult execute(ITask task, Serializable idObject) {
		return execute(idObject);
	}

	@Override
	public String getDescription() {
		return "This task succeeds only when the field nextStatus has the expected value";
	}

	@Override
	public Serializable getObject(ITask task) {
		return task.getIdObject();
	}

}
