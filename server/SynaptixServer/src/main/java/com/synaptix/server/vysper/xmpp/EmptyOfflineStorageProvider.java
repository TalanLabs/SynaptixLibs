package com.synaptix.server.vysper.xmpp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.vysper.xmpp.modules.extension.xep0160_offline_storage.OfflineStorageProvider;
import org.apache.vysper.xmpp.stanza.Stanza;

public class EmptyOfflineStorageProvider implements OfflineStorageProvider {

	private List<Stanza> emptyStanza = new ArrayList<Stanza>();

	@Override
	public void receive(Stanza stanza) {

	}

	@Override
	public Collection<Stanza> getStanzasForBareJID(String bareJID) {
		return emptyStanza;
	}
}
