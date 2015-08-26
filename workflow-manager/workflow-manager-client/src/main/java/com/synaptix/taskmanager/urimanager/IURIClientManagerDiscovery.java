package com.synaptix.taskmanager.urimanager;

import java.net.URI;

public interface IURIClientManagerDiscovery {

	/**
	 * Get a uriAction service by code
	 * 
	 * @param uriCode
	 * @return
	 */
	public abstract com.synaptix.taskmanager.urimanager.uriaction.IURIClientManager getURIClientManager(URI uri);

}