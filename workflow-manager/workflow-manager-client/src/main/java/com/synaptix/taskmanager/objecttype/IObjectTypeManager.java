package com.synaptix.taskmanager.objecttype;

import java.util.List;

import com.synaptix.taskmanager.model.ITaskObject;

public interface IObjectTypeManager {

	public <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> void addObjectType(Class<F> taskObjectClass,
			IObjectType<E, F, ManagerRole, ExecutantRole> objectType);

	public List<Class<? extends ITaskObject<?>>> getAllTaskObjectClasss();

	public <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> IObjectType<E, F, ManagerRole, ExecutantRole> getObjectType(
			Class<F> taskObjectClass);

	public IObjectType<?, ?, ?, ?> getObjectType2(Class<? extends ITaskObject<?>> taskObjectClass);
}
