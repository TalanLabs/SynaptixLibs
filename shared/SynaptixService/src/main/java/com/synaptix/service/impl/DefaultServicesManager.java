package com.synaptix.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synaptix.service.IServiceFactory;
import com.synaptix.service.IServiceFactoryListener;
import com.synaptix.service.NotFoundServiceFactoryException;
import com.synaptix.service.ServicesManager;

public class DefaultServicesManager extends ServicesManager {

	private Map<String, IServiceFactory> map;

	private List<IServiceFactoryListener> listener;

	public DefaultServicesManager() {
		super();

		map = new HashMap<String, IServiceFactory>();
		listener = new ArrayList<IServiceFactoryListener>();
	}

	@Override
	public IServiceFactory getServiceFactory(String name) {
		if (!map.containsKey(name)) {
			throw new NotFoundServiceFactoryException(name + " not exist");
		}
		return map.get(name);
	}

	@Override
	public void addServiceFactory(String name, IServiceFactory serviceFactory) {
		if (name == null || serviceFactory == null) {
			throw new NullPointerException("name or servicefactory is null");
		}
		map.put(name, serviceFactory);

		fireAdded(name, serviceFactory);
	}

	@Override
	public String[] getServiceFactoryNames() {
		Collection<String> c = map.keySet();
		return c.toArray(new String[c.size()]);
	}

	@Override
	public IServiceFactory[] getServiceFactories() {
		Collection<IServiceFactory> c = map.values();
		return c.toArray(new IServiceFactory[c.size()]);
	}

	@Override
	public void addServiceFactoryListener(IServiceFactoryListener l) {
		synchronized (listener) {
			listener.add(l);
		}
	}

	@Override
	public void removeServiceFactoryListener(IServiceFactoryListener l) {
		synchronized (listener) {
			listener.remove(l);
		}
	}

	protected synchronized void fireAdded(String name, IServiceFactory serviceFactory) {
		synchronized (listener) {
			for (IServiceFactoryListener l : listener) {
				l.added(name, serviceFactory);
			}
		}
	}
}
