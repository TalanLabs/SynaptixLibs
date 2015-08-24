package com.synaptix.taskmanager.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.objecttype.IObjectType;
import com.synaptix.taskmanager.urimanager.uriaction.IURIClientManager;

public abstract class AbstractTaskManagerClientModule extends AbstractModule {

	private MapBinder<Class<? extends ITaskObject<?>>, IObjectType<?, ?, ?, ?>> taskObjectTypeMapBinder;

	private Multibinder<IURIClientManager> uriClientManagerMultibinder;

	/**
	 * Get uri action multibinder
	 *
	 * @return
	 */
	protected final Multibinder<IURIClientManager> getURIClientManagerMultibinder() {
		if (uriClientManagerMultibinder == null) {
			uriClientManagerMultibinder = Multibinder.newSetBinder(binder(), IURIClientManager.class);
		}
		return uriClientManagerMultibinder;
	}

	/**
	 * Bind an uri action
	 */
	protected final void bindURIClientManager(Class<? extends IURIClientManager> uriClientManagerClass) {
		getURIClientManagerMultibinder().addBinding().to(uriClientManagerClass).in(Singleton.class);
	}

	/**
	 * Get task map binder multibinder
	 */
	protected final MapBinder<Class<? extends ITaskObject<?>>, IObjectType<?, ?, ?, ?>> getTaskObjectTypeMapBinder() {
		if (taskObjectTypeMapBinder == null) {
			taskObjectTypeMapBinder = MapBinder.newMapBinder(binder(), new TypeLiteral<Class<? extends ITaskObject<?>>>() {
			  }, new TypeLiteral<IObjectType<?, ?, ?, ?>>() {
			  }
			);
		}
		return taskObjectTypeMapBinder;
	}

	/**
	 * Bind a task object
	 */
	protected final <E extends Enum<E>, F extends ITaskObject<E>> void bindTaskObjectType(Class<F> taskObjectClass, Class<? extends IObjectType<E, F, ?, ?>> objectTypeClass) {
		getTaskObjectTypeMapBinder().addBinding(taskObjectClass).to(objectTypeClass).in(Singleton.class);
	}
}
