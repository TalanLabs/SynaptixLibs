package com.synaptix.server.vysper.xmpp.modules;

import java.util.List;

import org.apache.vysper.xmpp.modules.DefaultDiscoAwareModule;
import org.apache.vysper.xmpp.protocol.HandlerDictionary;
import org.apache.vysper.xmpp.protocol.NamespaceHandlerDictionary;

import com.google.inject.Inject;

public class SynaptixServerVersionModule extends DefaultDiscoAwareModule {

	private final SynaptixServerVersionIQHandler synaptixVersionIQHandler;

	@Inject
	public SynaptixServerVersionModule(SynaptixServerVersionIQHandler synaptixVersionIQHandler) {
		super();
		this.synaptixVersionIQHandler = synaptixVersionIQHandler;
	}

	@Override
	public String getName() {
		return "Synaptix Version";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	protected void addHandlerDictionaries(List<HandlerDictionary> dictionary) {
		dictionary.add(new NamespaceHandlerDictionary(SynaptixServerVersionIQHandler.SYNAPTIX_VERSION_IQ, synaptixVersionIQHandler));
	}
}
