package com.synaptix.server.vysper.xmpp.modules;

import java.util.List;

import org.apache.vysper.xmpp.modules.DefaultDiscoAwareModule;
import org.apache.vysper.xmpp.protocol.HandlerDictionary;
import org.apache.vysper.xmpp.protocol.NamespaceHandlerDictionary;

import com.google.inject.Inject;

public class SynaptixServiceModule extends DefaultDiscoAwareModule {

	private final SynaptixServiceIQHandler synaptixServiceIQHandler;

	@Inject
	public SynaptixServiceModule(SynaptixServiceIQHandler synaptixServiceIQHandler) {
		super();
		this.synaptixServiceIQHandler = synaptixServiceIQHandler;
	}

	@Override
	public String getName() {
		return "Synaptix Service";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	protected void addHandlerDictionaries(List<HandlerDictionary> dictionary) {
		dictionary.add(new NamespaceHandlerDictionary(SynaptixServiceIQHandler.SYNAPTIX_SERVICE_IQ, synaptixServiceIQHandler));
	}
}
