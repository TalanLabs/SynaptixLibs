package com.synaptix.smackx.version.packet;

import org.jivesoftware.smack.packet.IQ;

public class RequestServerVersionIQ extends ServerVersionIQ {

	public RequestServerVersionIQ() {
		super();

		this.setType(IQ.Type.GET);
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<request xmlns=\"synaptix.version:iq\">");
		sb.append("</request>");
		return sb.toString();
	}
}
