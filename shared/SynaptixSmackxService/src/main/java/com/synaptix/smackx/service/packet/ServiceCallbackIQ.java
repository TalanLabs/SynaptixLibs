package com.synaptix.smackx.service.packet;

import org.jivesoftware.smack.packet.IQ;

public abstract class ServiceCallbackIQ extends IQ {

	public ServiceCallbackIQ() {
		super();
	}

	protected abstract String toXMLExtension();

	public String getChildElementXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<query xmlns=\"synaptix.service.callback:iq\">");
		sb.append(toXMLExtension());
		sb.append("</query>");
		return sb.toString();
	}
}
