package com.synaptix.taskmanager.urimanager.uriaction;

import java.net.URI;

public abstract class AbstractSimpleURIClientManager extends AbstractURIClientManager {

	private String uriPath;

	public AbstractSimpleURIClientManager(String uriPath) {
		super();
		this.uriPath = uriPath;
	}

	@Override
	public final boolean acceptUri(URI uri) {
		return uri != null && uri.getPath().equals(uriPath);
	}
}
