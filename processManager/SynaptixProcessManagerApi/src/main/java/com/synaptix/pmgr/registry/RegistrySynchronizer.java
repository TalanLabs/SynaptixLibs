package com.synaptix.pmgr.registry;

public interface RegistrySynchronizer {
	public void activate();

	public void deactivate();

	public void listen();

	public void sendRequest(String service);

	public void sendProviderReg(String service, ServiceProvider provider);

	public void sendProviderUnreg(String service, ServiceProvider provider);

	public void sendServiceOverload(String service);

	public void sendServiceUnderload(String service);

	public void sendServiceIdling(String service);

	public void sendServiceBusy(String service);

	public void setSynchronizedRegistry(SynchronizedRegistry registry);
}
