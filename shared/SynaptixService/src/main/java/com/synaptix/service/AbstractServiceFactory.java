package com.synaptix.service;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractServiceFactory implements IServiceFactory {

	private String scope;

	private List<IServiceInterceptor> listener;

	public AbstractServiceFactory(String scope) {
		super();

		this.scope = scope;

		listener = new ArrayList<IServiceInterceptor>();
	}

	@Override
	public String getScope() {
		return scope;
	}

	@Override
	public void addServiceInterceptor(IServiceInterceptor l) {
		synchronized (listener) {
			listener.add(l);
		}
	}

	@Override
	public void removeServiceInterceptor(IServiceInterceptor l) {
		synchronized (listener) {
			listener.remove(l);
		}
	}

	protected void fireInterceptService(String serviceName, String methodName,
			Class<?>[] argTypes, Object[] args) {
		synchronized (listener) {
			for (IServiceInterceptor l : listener) {
				l.interceptService(serviceName, methodName, argTypes, args);
			}
		}
	}
}
