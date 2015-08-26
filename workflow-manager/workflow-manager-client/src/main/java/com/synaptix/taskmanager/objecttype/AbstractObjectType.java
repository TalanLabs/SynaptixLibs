package com.synaptix.taskmanager.objecttype;

import java.util.Map;

import com.synaptix.taskmanager.model.ITaskObject;

public abstract class AbstractObjectType<E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> implements
		IObjectType<E, F, ManagerRole, ExecutantRole> {

	protected final Class<E> statusClass;

	protected final Class<F> taskObjectClass;

	protected final Class<ManagerRole> managerRoleClass;

	protected final Class<ExecutantRole> executantRoleClass;

	public AbstractObjectType(Class<E> statusClass, Class<F> taskObjectClass, Class<ManagerRole> managerRoleClass, Class<ExecutantRole> executantRoleClass) {
		super();
		this.statusClass = statusClass;
		this.taskObjectClass = taskObjectClass;
		this.managerRoleClass = managerRoleClass;
		this.executantRoleClass = executantRoleClass;
	}

	@Override
	public final Class<E> getStatusClass() {
		return statusClass;
	}

	@Override
	public final String getStatusMeaning(E value) {
		return value != null ? getStatusMeaningMap().get(value.name()) : null;
	}

	protected abstract Map<String, String> getStatusMeaningMap();

	@Override
	public final Class<F> getTaskObjectClass() {
		return taskObjectClass;
	}

	@Override
	public final Class<ManagerRole> getManagerRoleClass() {
		return managerRoleClass;
	}

	@Override
	public final String getManagerRoleMeaning(ManagerRole value) {
		return value != null ? getManagerRoleMeaningMap().get(value.name()) : null;
	}

	protected abstract Map<String, String> getManagerRoleMeaningMap();

	@Override
	public final Class<ExecutantRole> getExecutantRoleClass() {
		return executantRoleClass;
	}

	@Override
	public final String getExecutantRoleMeaning(ExecutantRole value) {
		return value != null ? getExecutantRoleMeaningMap().get(value.name()) : null;
	}

	@Override
	public final String getTodoMeaning(String value) {
		return value != null ? getTodoDescriptionMap().get(value) : null;
	}

	protected abstract Map<String, String> getExecutantRoleMeaningMap();

	protected abstract Map<String, String> getTodoDescriptionMap();
}
