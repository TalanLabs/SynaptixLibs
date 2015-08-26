package com.synaptix.taskmanager.objecttype;

import java.net.URI;
import java.util.Iterator;
import java.util.Set;

import com.google.inject.Inject;
import com.synaptix.taskmanager.urimanager.IURIClientManagerDiscovery;
import com.synaptix.taskmanager.urimanager.uriaction.IURIClientManager;

public class DefaultURIClientManagerDiscovery implements IURIClientManagerDiscovery {

	@Inject(optional = true)
	private Set<IURIClientManager> uriActions;

	@Inject
	public DefaultURIClientManagerDiscovery() {
		super();
	}

	@Override
	public IURIClientManager getURIClientManager(URI uri) {
		IURIClientManager res = null;
		if (uriActions != null && !uriActions.isEmpty()) {
			Iterator<IURIClientManager> it = uriActions.iterator();
			while (it.hasNext() && res == null) {
				IURIClientManager uriAction = it.next();
				if (uriAction.acceptUri(uri)) {
					res = uriAction;
				}
			}
		}
		return res;
	}
}
