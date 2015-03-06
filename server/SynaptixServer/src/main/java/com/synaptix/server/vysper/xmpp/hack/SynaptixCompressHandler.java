package com.synaptix.server.vysper.xmpp.hack;

import org.apache.vysper.xmpp.protocol.ResponseStanzaContainer;
import org.apache.vysper.xmpp.protocol.ResponseStanzaContainerImpl;
import org.apache.vysper.xmpp.protocol.SessionStateHolder;
import org.apache.vysper.xmpp.protocol.StanzaHandler;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;

public class SynaptixCompressHandler implements StanzaHandler {

	public String getName() {
		return "compress";
	}

	public boolean verify(Stanza stanza) {
		if (stanza == null)
			return false;
		if (!getName().equals(stanza.getName()))
			return false;
		return true;
	}

	public boolean isSessionRequired() {
		return true;
	}

	public ResponseStanzaContainer execute(Stanza stanza, ServerRuntimeContext serverRuntimeContext, boolean isOutboundStanza, SessionContext sessionContext, SessionStateHolder sessionStateHolder) {
		return respondUnsupportedMethodFailure();

		// XMLElementVerifier xmlElementVerifier = stanza.getVerifier();
		// boolean compressNamespace = xmlElementVerifier.namespacePresent(SynaptixNamespaceURIs.JABBER_ORG_PROTOCOL_COMPRESS);
		//
		// if (!compressNamespace) {
		// return respondSetupFailedFailure();
		// }
		//
		// List<XMLElement> methodXmlElements = stanza.getInnerElementsNamed("method");
		// if (methodXmlElements == null || methodXmlElements.size() != 1) {
		// return respondSetupFailedFailure();
		// }
		//
		// XMLElement methodXmlElement = methodXmlElements.get(0);
		// if (methodXmlElement.getInnerText() != null && !"gaby".equals(methodXmlElement.getInnerText().getText())) {
		// return respondUnsupportedMethodFailure();
		// }
		//
		// StanzaBuilder stanzaBuilder = new StanzaBuilder("compressed", SynaptixNamespaceURIs.JABBER_ORG_PROTOCOL_COMPRESS);
		// Stanza responseStanza = stanzaBuilder.build();
		// sessionContext.putAttribute("first", true);
		// sessionContext.putAttribute("compressed", true);
		//
		// return new ResponseStanzaContainerImpl(responseStanza);
	}

	private ResponseStanzaContainer respondUnsupportedMethodFailure() {
		StanzaBuilder stanzaBuilder = new StanzaBuilder("failure", SynaptixNamespaceURIs.JABBER_ORG_PROTOCOL_COMPRESS);
		stanzaBuilder.startInnerElement("unsupported-method").endInnerElement();
		return new ResponseStanzaContainerImpl(stanzaBuilder.build());
	}

	private ResponseStanzaContainer respondSetupFailedFailure() {
		StanzaBuilder stanzaBuilder = new StanzaBuilder("failure", SynaptixNamespaceURIs.JABBER_ORG_PROTOCOL_COMPRESS);
		stanzaBuilder.startInnerElement("setup-failed").endInnerElement();
		return new ResponseStanzaContainerImpl(stanzaBuilder.build());
	}
}
