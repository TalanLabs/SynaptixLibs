package com.synaptix.smackx.abonnement.packet;

import org.jivesoftware.smack.packet.IQ;

public class RequestInfoAbonnementsGestionAbonnementIQ extends GestionAbonnementIQ {

	public RequestInfoAbonnementsGestionAbonnementIQ() {
		this.setType(IQ.Type.GET);
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<request xmlns=\"synaptix.abonnement:iq\">");
		sb.append("</request>");
		return sb.toString();
	}
}
