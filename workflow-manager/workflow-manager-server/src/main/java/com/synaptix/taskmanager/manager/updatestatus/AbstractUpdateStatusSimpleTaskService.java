package com.synaptix.taskmanager.manager.updatestatus;

import com.synaptix.taskmanager.manager.taskservice.AbstractSimpleTaskService;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.domains.ServiceNature;


public abstract class AbstractUpdateStatusSimpleTaskService<E extends Enum<E>, F extends ITaskObject<E>> extends AbstractSimpleTaskService<F> implements IUpdateStatusTaskService<E, F> {

	private final Class<E> statusClass;

	private final E newStatus;

	public AbstractUpdateStatusSimpleTaskService(String code, Class<E> statusClass, Class<F> objectType, E newStatus) {
		super(code, objectType, ServiceNature.UPDATE_STATUS);

		this.statusClass = statusClass;
		this.newStatus = newStatus;
	}

	@Override
	public final E getNewStatus() {
		return newStatus;
	}

	@Override
	public final Class<E> getStatusClass() {
		return statusClass;
	}

	@Override
	public String getDescription() {
		return new StringBuilder("Update status for ").append(getObjectType().getSimpleName()).append(" to ").append(newStatus).toString();
	}
}
