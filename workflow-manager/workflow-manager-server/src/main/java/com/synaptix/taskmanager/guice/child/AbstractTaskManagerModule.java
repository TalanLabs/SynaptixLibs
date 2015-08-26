package com.synaptix.taskmanager.guice.child;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.synaptix.taskmanager.manager.IObjectTypeTaskFactory;
import com.synaptix.taskmanager.manager.taskservice.ITaskService;
import com.synaptix.taskmanager.model.ITaskObject;

public abstract class AbstractTaskManagerModule extends AbstractModule {

	private MapBinder<Class<? extends ITaskObject<?>>, IObjectTypeTaskFactory<?>> objectFactoryMapBinder;

	private Multibinder<ITaskService> taskServiceMultibinder;

	/**
	 * Get object factory map binder
	 *
	 * @return
	 */
	protected final MapBinder<Class<? extends ITaskObject<?>>, IObjectTypeTaskFactory<?>> getObjectTypeTaskFactoryMapBinder() {
		if (objectFactoryMapBinder == null) {
			objectFactoryMapBinder = MapBinder.newMapBinder(binder(), new TypeLiteral<Class<? extends ITaskObject<?>>>() {
			}, new TypeLiteral<IObjectTypeTaskFactory<?>>() {
			});
		}
		return objectFactoryMapBinder;
	}

	/**
	 * Bind a object type task factory
	 *
	 * @param taskObjectClass
	 * @param objectTypeTaskFactoryClass
	 */
	protected final <E extends ITaskObject<?>> void bindObjectTypeTaskFactory(Class<E> taskObjectClass, Class<? extends IObjectTypeTaskFactory<E>> objectTypeTaskFactoryClass) {
		getObjectTypeTaskFactoryMapBinder().addBinding(taskObjectClass).to(objectTypeTaskFactoryClass).in(Singleton.class);
	}

	/**
	 * Get task service multibinder
	 *
	 * @return
	 */
	protected final Multibinder<ITaskService> getTaskServiceMultibinder() {
		if (taskServiceMultibinder == null) {
			taskServiceMultibinder = Multibinder.newSetBinder(binder(), new TypeLiteral<ITaskService>() {
			});
		}
		return taskServiceMultibinder;
	}

	/**
	 * Bind a task service
	 *
	 * @param taskServiceClass
	 */
	protected final void bindTaskService(Class<? extends ITaskService> taskServiceClass) {
		getTaskServiceMultibinder().addBinding().to(taskServiceClass).in(Singleton.class);
	}
}
