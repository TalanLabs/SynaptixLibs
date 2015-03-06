package com.synaptix.server.vysper.xmpp.hack;

import org.apache.vysper.xmpp.modules.extension.xep0077_inbandreg.InBandRegistrationModule;
import org.apache.vysper.xmpp.protocol.NamespaceURIs;
import org.apache.vysper.xmpp.server.SessionContext;
import org.apache.vysper.xmpp.server.response.ServerResponses;
import org.apache.vysper.xmpp.stanza.Stanza;
import org.apache.vysper.xmpp.stanza.StanzaBuilder;

public class SynaptixServerResponses extends ServerResponses {

	public Stanza getFeaturesForEncryption(SessionContext sessionContext) {
		StanzaBuilder stanzaBuilder = startFeatureStanza();
		stanzaBuilder.startInnerElement("starttls", NamespaceURIs.URN_IETF_PARAMS_XML_NS_XMPP_TLS);
		if (sessionContext.getServerRuntimeContext().getServerFeatures().isStartTLSRequired()) {
			stanzaBuilder.startInnerElement("required", NamespaceURIs.URN_IETF_PARAMS_XML_NS_XMPP_TLS).endInnerElement();
		}
		stanzaBuilder.endInnerElement();
		if (sessionContext.getServerRuntimeContext().getModule(InBandRegistrationModule.class) != null) {
			// In-band registration active, show as feature
			stanzaBuilder.startInnerElement("register", NamespaceURIs.JABBER_ORG_FEATURES_IQ_REGISTER);
		}

		stanzaBuilder.startInnerElement("compression", SynaptixNamespaceURIs.JABBER_ORG_FEATURES_COMPRESS);
		// TODO rendre generique
		// stanzaBuilder.startInnerElement("method").addText("zlib").endInnerElement();
		stanzaBuilder.startInnerElement("method").addText("gaby").endInnerElement();
		stanzaBuilder.endInnerElement();

		return stanzaBuilder.build();
	}

}
