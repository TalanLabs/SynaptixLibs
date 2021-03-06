package com.synaptix.server.service.guice;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public abstract class AbstractSynaptixServerServiceModule extends AbstractModule {

	/**
	 * Bind a service delegate
	 */
	protected final void bindDelegate(Class<?> serviceDelegateClass) {
		Preconditions.checkArgument(serviceDelegateClass != null, "Parameter 'serviceDelegateClass' must not be null");
		bind(serviceDelegateClass).in(Singleton.class);
	}

	/**
	 * Bind a service delegate
	 */
	protected final <E, F extends E> void bindDelegate(Class<E> serviceDelegateClass, Class<F> serviceDelegateImplClass) {
		Preconditions.checkArgument(serviceDelegateClass != null, "Parameter 'serviceDelegateClass' must not be null");
		Preconditions.checkArgument(serviceDelegateImplClass != null, "Parameter 'serviceDelegateImplClass' must not be null");
		bind(serviceDelegateImplClass).in(Singleton.class);
		bind(serviceDelegateClass).to(serviceDelegateImplClass).in(Singleton.class);
	}

	/**
	 * Bind a synaptix service
	 */
	protected final <F> ServiceBindingBuilder<F> bindService(final Class<F> implServiceClass) {
		Preconditions.checkArgument(implServiceClass != null, "Parameter 'implServiceClass' must not be null");
		bind(implServiceClass).in(Singleton.class);
		return new ServiceBindingBuilder<F>() {
			@Override
			public ServiceBindingBuilder<F> with(Class<? super F> serviceClass) {
				bindService(serviceClass, implServiceClass);
				return this;
			}
		};
	}

	private final <E, F extends E> void bindService(Class<E> serviceClass, Class<F> implServiceClass) {
		bind(serviceClass).toProvider(new SynaptixServiceProvider<E, F>(serviceClass, implServiceClass)).in(Singleton.class);
	}
}
