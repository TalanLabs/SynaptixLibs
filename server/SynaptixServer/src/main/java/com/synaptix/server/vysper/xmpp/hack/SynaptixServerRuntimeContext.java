package com.synaptix.server.vysper.xmpp.hack;

import java.util.List;

import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.delivery.StanzaRelay;
import org.apache.vysper.xmpp.protocol.HandlerDictionary;
import org.apache.vysper.xmpp.protocol.StanzaProcessor;
import org.apache.vysper.xmpp.server.DefaultServerRuntimeContext;
import org.apache.vysper.xmpp.server.ServerFeatures;
import org.apache.vysper.xmpp.state.resourcebinding.ResourceRegistry;

public class SynaptixServerRuntimeContext extends DefaultServerRuntimeContext {

	private StanzaProcessor stanzaProcessor;

	public SynaptixServerRuntimeContext(Entity serverEntity, StanzaProcessor stanzaProcessor, StanzaRelay stanzaRelay, ServerFeatures serverFeatures, List<HandlerDictionary> dictionaries,
			ResourceRegistry resourceRegistry) {
		super(serverEntity, stanzaRelay, serverFeatures, dictionaries, resourceRegistry);

		this.stanzaProcessor = stanzaProcessor;
	}

	public StanzaProcessor getStanzaProcessor() {
		return stanzaProcessor;
	}
}
