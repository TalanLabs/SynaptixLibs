package com.synaptix.server.vysper.xmpp.modules;

import org.apache.vysper.xml.fragment.XMLElement;
import org.apache.vysper.xmpp.modules.core.base.handler.DefaultIQHandler;
import org.apache.vysper.xmpp.server.ServerRuntimeContext;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.stanza.IQStanza;
import org.apache.vysper.xmpp.stanza.IQStanzaType;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SynaptixServerVersionIQHandler extends DefaultIQHandler {

	public static final String SYNAPTIX_VERSION_IQ = "synaptix.server.version:iq";

	private final String serverVersion;

	@Inject
	public SynaptixServerVersionIQHandler(@Named("serverVersion") String serverVersion) {
		super();

		this.serverVersion = serverVersion;
	}

	@Override
	protected boolean verifyNamespace(Stanza stanza) {
		return verifyInnerNamespace(stanza, SYNAPTIX_VERSION_IQ);
	}

	@Override
	protected Stanza handleGet(IQStanza stanza, ServerRuntimeContext serverRuntimeContext, SessionContext sessionContext) {
		XMLElement requestXMLElement = stanza.getFirstInnerElement().getFirstInnerElement();
		if (requestXMLElement != null && "request".equals(requestXMLElement.getName())) {
			StanzaBuilder stanzaBuilder = StanzaBuilder.createIQStanza(stanza.getTo(), stanza.getFrom(), IQStanzaType.RESULT, stanza.getID());
			stanzaBuilder.startInnerElement("query", SYNAPTIX_VERSION_IQ);
			stanzaBuilder.startInnerElement("result", SYNAPTIX_VERSION_IQ);
			stanzaBuilder.startInnerElement("serverVersion", SYNAPTIX_VERSION_IQ);
			stanzaBuilder.addText(serverVersion);
			stanzaBuilder.endInnerElement();
			stanzaBuilder.endInnerElement();
			stanzaBuilder.endInnerElement();
			return stanzaBuilder.build();
		}
		return super.handleGet(stanza, serverRuntimeContext, sessionContext);
	}
}
