package com.synaptix.pmgr.registry;

import java.util.List;

public interface SynchronizedRegistry extends Registry {
	public void setSynchronizer(RegistrySynchronizer synchronizer);

	public RegistrySynchronizer getSynchronizer();

	public ServiceProvider getProviderProxy(String providerURI);

	public List<ServiceProvider> getProviders(String service,
			boolean requestSync);
}
