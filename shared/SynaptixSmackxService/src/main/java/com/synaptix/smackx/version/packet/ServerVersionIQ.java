package com.synaptix.smackx.version.packet;

import org.jivesoftware.smack.packet.IQ;

public abstract class ServerVersionIQ extends IQ {

	public ServerVersionIQ() {
		super();
	}

	protected abstract String toXMLExtension();

	public String getChildElementXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<query xmlns=\"synaptix.version:iq\">");
		sb.append(toXMLExtension());
		sb.append("</query>");
		return sb.toString();
	}

}
