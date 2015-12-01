package com.synaptix.taskmanager.manager.taskservice;

import com.google.inject.Inject;
import com.synaptix.entity.IId;
import com.synaptix.mybatis.dao.IDaoSession;
import com.synaptix.service.ServiceException;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.domains.ServiceNature;

public abstract class AbstractCheckNextStatusTaskService extends AbstractTaskService<IId> {

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

	protected abstract IExecutionResult execute(IId idObject);

	@Override
	public IExecutionResult execute(ITask task, IId idObject) {
		return execute(idObject);
	}

	@Override
	public String getDescription() {
		return "This task succeeds only when the field nextStatus has the expected value";
	}

	@Override
	public IId getObject(ITask task) {
		return task.getIdObject();
	}

}
