package com.synaptix.smackx.abonnement.packet;

import org.jivesoftware.smack.packet.IQ;

public abstract class GestionAbonnementIQ  extends IQ {

	public GestionAbonnementIQ() {
		super();
	}

	protected abstract String toXMLExtension();

	public String getChildElementXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<query xmlns=\"synaptix.abonnement:iq\">");
		sb.append(toXMLExtension());
		sb.append("</query>");
		return sb.toString();
	}

}
