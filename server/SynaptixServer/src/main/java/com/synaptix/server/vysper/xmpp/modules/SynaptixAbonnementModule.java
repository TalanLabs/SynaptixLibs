package com.synaptix.server.vysper.xmpp.modules;

import java.util.List;

import javax.inject.Inject;

import org.apache.vysper.xmpp.modules.DefaultDiscoAwareModule;
import org.apache.vysper.xmpp.protocol.HandlerDictionary;
import org.apache.vysper.xmpp.protocol.NamespaceHandlerDictionary;

public class SynaptixAbonnementModule extends DefaultDiscoAwareModule {

	private final SynaptixAbonnementIQHandler synaptixAbonnementIQHandler;

	@Inject
	public SynaptixAbonnementModule(SynaptixAbonnementIQHandler synaptixAbonnementIQHandler) {
		super();
		this.synaptixAbonnementIQHandler = synaptixAbonnementIQHandler;
	}

	@Override
	public String getName() {
		return "Synaptix Abonnement";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	protected void addHandlerDictionaries(List<HandlerDictionary> dictionary) {
		dictionary.add(new NamespaceHandlerDictionary(SynaptixAbonnementIQHandler.SYNAPTIX_ABONNEMENT_IQ, synaptixAbonnementIQHandler));
	}
}