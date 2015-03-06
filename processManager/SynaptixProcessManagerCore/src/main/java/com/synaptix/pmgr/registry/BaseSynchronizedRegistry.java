package com.synaptix.pmgr.registry;

import java.util.List;

public class BaseSynchronizedRegistry extends AbstractRegistry implements
		RegistryListener, SynchronizedRegistry {

	RegistrySynchronizer synchronizer;

	public BaseSynchronizedRegistry() {
		super();
		addListener(this);
	}

	public BaseSynchronizedRegistry(String uid) {
		super(uid);
		addListener(this);
	}

	public BaseSynchronizedRegistry(String uid,
			RegistrySynchronizer synchronizer) {
		super(uid);
		addListener(this);
		this.synchronizer = synchronizer;
		if (synchronizer != null) {
			synchronizer.setSynchronizedRegistry(this);
			synchronizer.activate();
		}
	}

	public void setSynchronizer(RegistrySynchronizer synchronizer) {
		this.synchronizer = synchronizer;
		synchronizer.setSynchronizedRegistry(this);
		synchronizer.activate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.SynchronizedRegistry#getSynchronizer()
	 */
	public RegistrySynchronizer getSynchronizer() {
		return synchronizer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synaptix.registry.Registry#refresh()
	 */
	public void refresh() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.synaptix.registry.AbstractRegistry#getProviders(java.lang.String)
	 */
	public List<ServiceProvider> getProviders(String serviceid,
			boolean requestSync) {
		List<ServiceProvider> p = super.getProviders(serviceid);

		if ((requestSync) && ((p == null) || (p.size() == 0))) {
			requestProviders(serviceid);
		}
		return p;
	}

	private void requestProviders(String serviceid) {
		synchronizer.sendRequest(serviceid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.synaptix.registry.RegistryListener#notifyProviderReg(com.synaptix
	 * .registry.Registry, java.lang.String,
	 * com.synaptix.registry.ServiceProvider)
	 */
	public void notifyProviderReg(Registry registry, String serviceid,
			ServiceProvider provider) {
		if (logger != null) {
			logger.trace(registry.getUID() + " New bound provider : "
					+ serviceid + ", " + provider.getURI());
		} else {
			System.out.println("********(" + registry.getUID()
					+ ") new bound provider : " + serviceid + ", "
					+ provider.getURI());
		}
		if (provider.isLocal())
			synchronizer.sendProviderReg(serviceid, provider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.synaptix.registry.RegistryListener#notifyServiceOverload(com.synaptix
	 * .registry.Registry, java.lang.String)
	 */
	public void notifyServiceOverload(Registry registry, String serviceid) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.synaptix.registry.RegistryListener#notifyServiceUnderload(com.synaptix
	 * .registry.Registry, java.lang.String)
	 */
	public void notifyServiceUnderload(Registry registry, String serviceid) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.synaptix.registry.RegistryListener#notifyProviderUnreg(com.synaptix
	 * .registry.Registry, java.lang.String,
	 * com.synaptix.registry.ServiceProvider)
	 */
	public void notifyProviderUnreg(Registry registry, String serviceid,
			ServiceProvider provider) {
		synchronizer.sendProviderUnreg(serviceid, provider);
	}

	public void resyncProviders() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.synaptix.registry.SynchronizedRegistry#getProviderProxy(java.lang
	 * .String)
	 */
	public ServiceProvider getProviderProxy(String providerURI) {
		return new BaseServiceProvider(providerURI, false);
	}

}
