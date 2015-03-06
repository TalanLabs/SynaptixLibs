package com.synaptix.service;

import com.synaptix.service.impl.DefaultServicesManager;

public abstract class ServicesManager {

	private static ServicesManager instance;

	public static ServicesManager getInstance() {
		if (instance == null) {
			instance = new DefaultServicesManager();
		}
		return instance;
	}

	/**
	 * Set a custom servicesmanager
	 * 
	 * @param instance
	 */
	public static void setInstance(ServicesManager instance) {
		ServicesManager.instance = instance;
	}

	/**
	 * Get service factory
	 * 
	 * @param name
	 * @return IServiceFactory
	 * @exception NotFoundServiceFactoryException
	 */
	public abstract IServiceFactory getServiceFactory(String name);

	/**
	 * Add service factory
	 * 
	 * @param name
	 * @param serviceFactory
	 */
	public abstract void addServiceFactory(String name, IServiceFactory serviceFactory);

	/**
	 * Get all names service factory
	 * 
	 * @return
	 */
	public abstract String[] getServiceFactoryNames();

	/**
	 * Get all service factory
	 * 
	 * @return
	 */
	public abstract IServiceFactory[] getServiceFactories();

	/**
	 * Add listener
	 * 
	 * @param l
	 */
	public abstract void addServiceFactoryListener(IServiceFactoryListener l);

	/**
	 * Remove listener
	 * 
	 * @param l
	 */
	public abstract void removeServiceFactoryListener(IServiceFactoryListener l);
}
