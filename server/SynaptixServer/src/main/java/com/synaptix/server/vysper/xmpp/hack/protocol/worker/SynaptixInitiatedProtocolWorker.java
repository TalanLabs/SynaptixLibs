package com.synaptix.server.vysper.xmpp.hack.protocol.worker;

import org.apache.vysper.xmpp.modules.core.base.handler.StreamStartHandler;
import org.apache.vysper.xmpp.modules.core.base.handler.XMLPrologHandler;
import org.apache.vysper.xmpp.protocol.ResponseWriter;
import org.apache.vysper.xmpp.protocol.SessionStateHolder;
import org.apache.vysper.xmpp.protocol.StanzaHandler;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.server.SessionState;
import org.apache.vysper.xmpp.stanza.Stanza;

public class SynaptixInitiatedProtocolWorker extends AbstractSynaptixStateAwareProtocolWorker {

	@Override
	public SessionState getHandledState() {
		return SessionState.INITIATED;
	}

	@Override
	protected boolean checkState(SessionContext sessionContext, SessionStateHolder sessionStateHolder, Stanza stanza, StanzaHandler stanzaHandler) {
		if (stanzaHandler instanceof XMLPrologHandler)
			return true;
		if (stanzaHandler instanceof StreamStartHandler)
			return true;
		if (super.checkState(sessionContext, sessionStateHolder, stanza, stanzaHandler)) {
			return true;
		}
		ResponseWriter.writeUnsupportedStanzaError(sessionContext);
		return false;
	}

}
