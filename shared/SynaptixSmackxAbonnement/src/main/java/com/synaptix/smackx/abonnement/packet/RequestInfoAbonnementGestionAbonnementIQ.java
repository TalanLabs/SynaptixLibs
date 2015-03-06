package com.synaptix.smackx.abonnement.packet;

import org.jivesoftware.smack.packet.IQ;

public class RequestInfoAbonnementGestionAbonnementIQ extends GestionAbonnementIQ {

	private String name;

	public RequestInfoAbonnementGestionAbonnementIQ(String name) {
		this.setType(IQ.Type.GET);

		this.name = name;
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<request xmlns=\"synaptix.abonnement:iq\"");
		sb.append(" name=\"");
		sb.append(name);
		sb.append("\">");
		sb.append("</request>");
		return sb.toString();
	}

}
