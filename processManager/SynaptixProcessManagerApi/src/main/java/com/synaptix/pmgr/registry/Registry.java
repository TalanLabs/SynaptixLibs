package com.synaptix.pmgr.registry;

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;

public interface Registry {

	public String getUID();

	public void refresh();

	public Collection<String> getServices();

	public List<ServiceProvider> getProviders(String serviceid);

	public void registerProvider(String serviceid, ServiceProvider provider);

	public void unregisterProvider(String serviceid, ServiceProvider provider);

	public void unregisterService(String serviceid);

	public void setServiceOverload(String serviceid);

	public void setServiceUnderload(String serviceid);

	public void addListener(RegistryListener listener);

	public void setLogger(Log logger);

	public Log getLogger();
}
