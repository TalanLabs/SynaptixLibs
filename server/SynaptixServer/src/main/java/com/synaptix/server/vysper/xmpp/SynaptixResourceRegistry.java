package com.synaptix.server.vysper.xmpp;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.state.resourcebinding.ResourceRegistry;

public class SynaptixResourceRegistry extends ResourceRegistry {

	/**
	 * Get all session context
	 * 
	 * @return
	 */
	public Set<SessionContext> getAllSessionContext() {
		return Collections.unmodifiableSet(sessionResources.keySet());
	}

	/**
	 * Get a session context by session id
	 * 
	 * @param sessionId
	 * @return
	 */
	public SessionContext getSession(String sessionId) {
		SessionContext res = null;
		Iterator<SessionContext> it = sessionResources.keySet().iterator();
		while (it.hasNext() && res == null) {
			SessionContext sc = it.next();
			if (sessionId.equals(sc.getSessionId())) {
				res = sc;
			}
		}
		return res;
	}

}
