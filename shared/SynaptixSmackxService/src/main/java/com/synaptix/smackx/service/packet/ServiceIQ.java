package com.synaptix.smackx.service.packet;

import org.jivesoftware.smack.packet.IQ;

public abstract class ServiceIQ extends IQ {

	public ServiceIQ() {
		super();
	}

	protected abstract String toXMLExtension();

	public String getChildElementXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<query xmlns=\"synaptix.service:iq\">");
		sb.append(toXMLExtension());
		sb.append("</query>");
		return sb.toString();
	}
}
