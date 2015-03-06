package com.synaptix.server.vysper.xmpp.hack.protocol.worker;

import org.apache.vysper.xmpp.modules.core.base.handler.StreamStartHandler;
import org.apache.vysper.xmpp.modules.core.base.handler.XMLPrologHandler;
import org.apache.vysper.xmpp.modules.core.sasl.handler.AbstractSASLHandler;
import org.apache.vysper.xmpp.modules.extension.xep0077_inbandreg.InBandRegistrationHandler;
import org.apache.vysper.xmpp.modules.extension.xep0220_server_dailback.DbResultHandler;
import org.apache.vysper.xmpp.modules.extension.xep0220_server_dailback.DbVerifyHandler;
import org.apache.vysper.xmpp.protocol.ResponseWriter;
import org.apache.vysper.xmpp.protocol.SessionStateHolder;
import org.apache.vysper.xmpp.protocol.StanzaHandler;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.server.SessionState;
import org.apache.vysper.xmpp.stanza.Stanza;

public class SynaptixEncryptedProtocolWorker extends AbstractSynaptixStateAwareProtocolWorker {

	@Override
	public SessionState getHandledState() {
		return SessionState.ENCRYPTED;
	}

	@Override
	protected boolean checkState(SessionContext sessionContext, SessionStateHolder sessionStateHolder, Stanza stanza, StanzaHandler stanzaHandler) {

		if (stanzaHandler instanceof StreamStartHandler) {
			return true;
		} else if (stanzaHandler instanceof AbstractSASLHandler) {
			return true;
		} else if (stanzaHandler instanceof XMLPrologHandler) {
			return true; // PSI client sends that.
		} else if (stanzaHandler instanceof InBandRegistrationHandler) {
			return true;
		} else if (sessionContext.isServerToServer() && stanzaHandler instanceof DbResultHandler) {
			return true;
		} else if (sessionContext.isServerToServer() && stanzaHandler instanceof DbVerifyHandler) {
			return true;
		}
		if (super.checkState(sessionContext, sessionStateHolder, stanza, stanzaHandler)) {
			return true;
		}
		ResponseWriter.writeUnsupportedStanzaError(sessionContext);
		return false;
	}
}