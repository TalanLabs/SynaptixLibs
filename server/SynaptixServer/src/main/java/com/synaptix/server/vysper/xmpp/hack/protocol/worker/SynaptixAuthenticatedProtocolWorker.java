package com.synaptix.server.vysper.xmpp.hack.protocol.worker;

import org.apache.vysper.xmpp.modules.core.base.handler.StreamStartHandler;
import org.apache.vysper.xmpp.protocol.ResponseWriter;
import org.apache.vysper.xmpp.protocol.SessionStateHolder;
import org.apache.vysper.xmpp.protocol.StanzaHandler;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.server.SessionState;
import org.apache.vysper.xmpp.stanza.Stanza;

public class SynaptixAuthenticatedProtocolWorker extends AbstractSynaptixStateAwareProtocolWorker {

	@Override
	protected boolean checkState(SessionContext sessionContext, SessionStateHolder sessionStateHolder, Stanza stanza, StanzaHandler stanzaHandler) {
		if (stanzaHandler instanceof StreamStartHandler)
			return true;
		if (stanzaHandler.verify(stanza))
			return true;
		if (super.checkState(sessionContext, sessionStateHolder, stanza, stanzaHandler)) {
			return true;
		}
		ResponseWriter.writeUnsupportedStanzaError(sessionContext);
		return false;
	}

	@Override
	public SessionState getHandledState() {
		return SessionState.AUTHENTICATED;
	}

}