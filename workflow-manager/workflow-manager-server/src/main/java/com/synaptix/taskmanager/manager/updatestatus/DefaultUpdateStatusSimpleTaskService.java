package com.synaptix.taskmanager.manager.updatestatus;

import com.synaptix.service.ServiceException;
import com.synaptix.taskmanager.manager.taskservice.ExecutionResultBuilder;
import com.synaptix.taskmanager.model.ITaskObject;


public class DefaultUpdateStatusSimpleTaskService<E extends Enum<E>, F extends ITaskObject<E>> extends AbstractUpdateStatusSimpleTaskService<E, F> {

	public DefaultUpdateStatusSimpleTaskService(String code, Class<E> statusClass, Class<F> objectType, E newStatus) {
		super(code, statusClass, objectType, newStatus);
	}

	@Override
	protected final IExecutionResult execute(F taskObject) {
		IExecutionResult res = preExecute(taskObject);
		if (res != null && res.isFinished()) {
			taskObject.setStatus(getNewStatus());
			int saveResult = saveOrUpdateEntity(taskObject);
			if (saveResult != 1) {
				throw new ServiceException("SAVE_FAILED", null);
			}
		}
		return res;
	}

	protected IExecutionResult preExecute(F taskObject) {
		return new ExecutionResultBuilder().finished();
	}
}
