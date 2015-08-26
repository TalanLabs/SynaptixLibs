package com.synaptix.taskmanager.objecttype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NullArgumentException;

import com.google.inject.Inject;
import com.synaptix.taskmanager.model.ITaskObject;

public class DefaultObjectTypeManager implements IObjectTypeManager {

	@Inject
	private Map<Class<? extends ITaskObject<?>>, IObjectType<?, ?, ?, ?>> map;

	@Inject
	public DefaultObjectTypeManager() {
		super();
	}

	public <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> void addObjectType(Class<F> taskObjectClass,
			IObjectType<E, F, ManagerRole, ExecutantRole> objectType) {
		throw new IllegalAccessError("Do not use addObjectType(), use MapBinder");
	}

	@Override
	public List<Class<? extends ITaskObject<?>>> getAllTaskObjectClasss() {
		return Collections.unmodifiableList(new ArrayList<Class<? extends ITaskObject<?>>>(map.keySet()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E extends Enum<E>, F extends ITaskObject<E>, ManagerRole extends Enum<ManagerRole>, ExecutantRole extends Enum<ExecutantRole>> IObjectType<E, F, ManagerRole, ExecutantRole> getObjectType(
			Class<F> taskObjectClass) {
		if (taskObjectClass == null) {
			throw new NullArgumentException("taskObjectClass is null");
		}
		return (IObjectType<E, F, ManagerRole, ExecutantRole>) map.get(taskObjectClass);
	}

	@Override
	public IObjectType<?, ?, ?, ?> getObjectType2(Class<? extends ITaskObject<?>> taskObjectClass) {
		if (taskObjectClass == null) {
			throw new NullArgumentException("taskObjectClass is null");
		}
		return map.get(taskObjectClass);
	}
}
