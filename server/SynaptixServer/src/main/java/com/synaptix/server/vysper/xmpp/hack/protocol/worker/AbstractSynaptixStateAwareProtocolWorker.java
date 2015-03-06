package com.synaptix.server.vysper.xmpp.hack.protocol.worker;

import org.apache.vysper.xmpp.protocol.SessionStateHolder;
import org.apache.vysper.xmpp.protocol.StanzaHandler;
import org.apache.vysper.xmpp.protocol.worker.AbstractStateAwareProtocolWorker;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.stanza.Stanza;

import com.synaptix.server.vysper.xmpp.hack.IStateStanzaHandler;

public abstract class AbstractSynaptixStateAwareProtocolWorker extends AbstractStateAwareProtocolWorker {

	@Override
	protected boolean checkState(SessionContext sessionContext, SessionStateHolder sessionStateHolder, Stanza stanza, StanzaHandler stanzaHandler) {
		if (stanzaHandler instanceof IStateStanzaHandler) {
			IStateStanzaHandler stateStanzaHandler = (IStateStanzaHandler) stanzaHandler;
			return stateStanzaHandler.checkState(sessionContext, sessionStateHolder, stanza);
		}
		return super.checkState(sessionContext, sessionStateHolder, stanza, stanzaHandler);
	}

}
