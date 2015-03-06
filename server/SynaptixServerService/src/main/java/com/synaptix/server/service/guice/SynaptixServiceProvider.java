package com.synaptix.server.service.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.synaptix.server.service.GuiceServerServiceFactory;

public class SynaptixServiceProvider<E, F extends E> implements Provider<E> {

	private final Class<E> serviceClass;

	private final Class<F> implServiceClass;

	private GuiceServerServiceFactory guiceServerServiceFactory;

	public SynaptixServiceProvider(Class<E> serviceClass, Class<F> implServiceClass) {
		super();
		this.serviceClass = serviceClass;
		this.implServiceClass = implServiceClass;
	}

	@Inject
	public void setGuiceServerServiceFactory(GuiceServerServiceFactory guiceServerServiceFactory) {
		this.guiceServerServiceFactory = guiceServerServiceFactory;

		guiceServerServiceFactory.addService(serviceClass, implServiceClass);
	}

	@Override
	public E get() {
		return guiceServerServiceFactory.getService(serviceClass);
	}
}
