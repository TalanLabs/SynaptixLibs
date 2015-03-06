package com.synaptix.smackx.abonnement.packet;

import org.jivesoftware.smack.packet.IQ;

public class RequestRevoquerGestionAbonnementIQ extends GestionAbonnementIQ {

	private String name;

	public RequestRevoquerGestionAbonnementIQ(String name) {
		this.name = name;

		this.setType(IQ.Type.SET);
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<request xmlns=\"synaptix.abonnement:iq\"");
		sb.append(" name=\"");
		sb.append(name);
		sb.append("\"");
		sb.append(" type=\"revoquer\"");
		sb.append(">");
		sb.append("</request>");
		return sb.toString();
	}

}
