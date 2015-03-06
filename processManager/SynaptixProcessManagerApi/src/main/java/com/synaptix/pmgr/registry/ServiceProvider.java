package com.synaptix.pmgr.registry;

public interface ServiceProvider {
	public void attach(Registry registry, String service);

	public void detach(Registry registry, String service);

	public String getURI();

	public boolean isLocal();
}
