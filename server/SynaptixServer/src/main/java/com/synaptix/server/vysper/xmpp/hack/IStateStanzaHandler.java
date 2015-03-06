package com.synaptix.server.vysper.xmpp.hack;

import org.apache.vysper.xmpp.protocol.SessionStateHolder;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.stanza.Stanza;

public interface IStateStanzaHandler {

	public boolean checkState(SessionContext sessionContext, SessionStateHolder sessionStateHolder, Stanza stanza);

}
