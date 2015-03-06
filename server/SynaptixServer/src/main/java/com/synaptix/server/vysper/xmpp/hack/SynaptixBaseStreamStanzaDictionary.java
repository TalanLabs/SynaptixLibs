package com.synaptix.server.vysper.xmpp.hack;

import org.apache.vysper.xmpp.modules.core.base.handler.IQHandler;
import org.apache.vysper.xmpp.modules.core.base.handler.MessageHandler;
import org.apache.vysper.xmpp.modules.core.base.handler.XMLPrologHandler;
import org.apache.vysper.xmpp.modules.core.im.handler.PresenceHandler;
import org.apache.vysper.xmpp.protocol.NamespaceHandlerDictionary;
import org.apache.vysper.xmpp.protocol.NamespaceURIs;

public class SynaptixBaseStreamStanzaDictionary extends NamespaceHandlerDictionary {

	public SynaptixBaseStreamStanzaDictionary() {
		super(NamespaceURIs.HTTP_ETHERX_JABBER_ORG_STREAMS);
		register(new XMLPrologHandler());
		register(new SynaptixStreamStartHandler());
		register(new IQHandler());
		register(new MessageHandler());
		register(new PresenceHandler());
		seal();
	}
}
