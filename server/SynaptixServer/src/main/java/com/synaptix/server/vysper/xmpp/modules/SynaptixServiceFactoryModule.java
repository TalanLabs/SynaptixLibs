package com.synaptix.server.vysper.xmpp.modules;

import java.util.List;

import org.apache.vysper.xmpp.modules.DefaultDiscoAwareModule;
import org.apache.vysper.xmpp.protocol.HandlerDictionary;
import org.apache.vysper.xmpp.protocol.NamespaceHandlerDictionary;

import com.google.inject.Inject;

public class SynaptixServiceFactoryModule extends DefaultDiscoAwareModule {

	private final SynaptixServiceFactoryIQHandler synaptixServiceFactoryIQHandler;

	@Inject
	public SynaptixServiceFactoryModule(SynaptixServiceFactoryIQHandler synaptixServiceFactoryIQHandler) {
		super();
		this.synaptixServiceFactoryIQHandler = synaptixServiceFactoryIQHandler;
	}

	@Override
	public String getName() {
		return "Synaptix ServiceFactory";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	protected void addHandlerDictionaries(List<HandlerDictionary> dictionary) {
		dictionary.add(new NamespaceHandlerDictionary(SynaptixServiceFactoryIQHandler.SYNAPTIX_SERVICEFACTORY_IQ, synaptixServiceFactoryIQHandler));
	}
}
