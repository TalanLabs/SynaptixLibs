package com.synaptix.pmgr.registry;

import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

public abstract class AbstractRegistry implements Registry {

	String uid;
	Map<String, List<ServiceProvider>> providers;
	List<RegistryListener> regListeners;

	public Log logger;

	public AbstractRegistry() {
		uid = (new UID()).toString();
		providers = new HashMap<String, List<ServiceProvider>>();
		regListeners = new ArrayList<RegistryListener>();
		// logger = Logger.getLogger("Registry");
		logger = LogFactoryImpl.getLog(AbstractRegistry.class);
	}

	public Log getLogger() {
		return logger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.Registry#setLogger(java.util.logging.Logger)
	 */
	public void setLogger(Log logger) {
		this.logger = logger;
	}

	public AbstractRegistry(String uid) {
		this.uid = uid;
		providers = new HashMap();
		regListeners = new ArrayList<RegistryListener>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.Registry#getUID()
	 */
	public String getUID() {
		return uid;
	}

	public void addListener(RegistryListener listener) {
		regListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.Registry#getProviders(java.lang.String)
	 */
	public List<ServiceProvider> getProviders(String serviceid) {
		return providers.get(serviceid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.Registry#registerProvider(java.lang.String,
	 * java.lang.String)
	 */
	public void registerProvider(String serviceid, ServiceProvider provider) {
		List<ServiceProvider> p = getProviders(serviceid);
		if (p == null) {
			providers.put(serviceid, new ArrayList<ServiceProvider>());
			p = getProviders(serviceid);
		}
		if (p.add(provider))
			provider.attach(this, serviceid);
		for (int i = 0; i < regListeners.size(); i++) {
			regListeners.get(i).notifyProviderReg(this,
					serviceid, provider);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.synaptix.registry.Registry#notifyServiceOverload(java.lang.String)
	 */
	public void setServiceOverload(String serviceid) {
		for (int i = 0; i < regListeners.size(); i++) {
			((RegistryListener) regListeners.get(i)).notifyServiceOverload(
					this, serviceid);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.synaptix.registry.Registry#notifyServiceUnderload(java.lang.String)
	 */
	public void setServiceUnderload(String serviceid) {
		for (int i = 0; i < regListeners.size(); i++) {
			((RegistryListener) regListeners.get(i)).notifyServiceUnderload(
					this, serviceid);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.Registry#unregisterProvider(java.lang.String,
	 * java.lang.String)
	 */
	public void unregisterProvider(String serviceid, ServiceProvider provider) {
		for (int i = 0; i < regListeners.size(); i++) {
			((RegistryListener) regListeners.get(i)).notifyProviderReg(this,
					serviceid, provider);
		}
		List<ServiceProvider> p = getProviders(serviceid);
		if (p != null) {
			if (p.remove(provider)) {
				provider.detach(this, serviceid);
				if (p.size() == 0)
					providers.remove(serviceid);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.Registry#unregisterService(java.lang.String)
	 */
	public void unregisterService(String serviceid) {
		List<ServiceProvider> p = providers.remove(serviceid);
		if (p != null) {
			for (int i = 0; i < p.size(); i++) {
				((ServiceProvider) p.get(i)).detach(this, serviceid);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.Registry#getServices()
	 */
	public Collection<String> getServices() {

		return providers.keySet();
	}

}
