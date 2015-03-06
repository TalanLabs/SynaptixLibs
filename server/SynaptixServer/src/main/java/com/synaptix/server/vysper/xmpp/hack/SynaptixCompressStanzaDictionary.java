package com.synaptix.server.vysper.xmpp.hack;

import org.apache.vysper.xmpp.protocol.NamespaceHandlerDictionary;

public class SynaptixCompressStanzaDictionary extends NamespaceHandlerDictionary {

	public SynaptixCompressStanzaDictionary() {
		super(SynaptixNamespaceURIs.JABBER_ORG_PROTOCOL_COMPRESS);
		register(new SynaptixCompressHandler());
		seal();
	}
}
