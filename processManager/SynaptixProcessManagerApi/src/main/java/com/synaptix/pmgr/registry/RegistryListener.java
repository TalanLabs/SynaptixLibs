package com.synaptix.pmgr.registry;

public interface RegistryListener {
	public void notifyProviderReg(Registry registry, String serviceid,
			ServiceProvider provider);

	public void notifyProviderUnreg(Registry registry, String serviceid,
			ServiceProvider provider);

	public void notifyServiceOverload(Registry registry, String serviceid);

	public void notifyServiceUnderload(Registry registry, String serviceid);
}
