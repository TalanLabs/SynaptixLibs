package com.synaptix.taskmanager.manager.taskservice;

import com.google.inject.Inject;
import com.synaptix.mybatis.delegate.EntityServiceDelegate;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.domains.ServiceNature;

public abstract class AbstractSimpleTaskService<F extends ITaskObject<?>> extends AbstractTaskService<F> {

	@Inject
	private EntityServiceDelegate entityServiceDelegate;

	public AbstractSimpleTaskService(String code, Class<F> objectType, ServiceNature nature) {
		super(code, nature, objectType);
		this.objectType = objectType;
	}

	private final Class<F> objectType;

	public final Class<F> getObjectType() {
		return objectType;
	}

	protected IExecutionResult execute(F taskObject) {
		return new ExecutionResultBuilder().finished();
	}

	@Override
	public IExecutionResult execute(ITask task, F object) {
		return execute(object);
	}

	@Override
	public F getObject(ITask task) {
		return entityServiceDelegate.findEntityById(getObjectType(), task.getIdObject());
	}
}
