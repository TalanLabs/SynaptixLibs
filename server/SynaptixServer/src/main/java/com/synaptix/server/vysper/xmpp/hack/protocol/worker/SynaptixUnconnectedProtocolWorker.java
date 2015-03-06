package com.synaptix.server.vysper.xmpp.hack.protocol.worker;

import org.apache.vysper.xmpp.server.SessionState;

public class SynaptixUnconnectedProtocolWorker extends AbstractSynaptixStateAwareProtocolWorker {

	@Override
	public SessionState getHandledState() {
		return SessionState.UNCONNECTED;
	}
}
