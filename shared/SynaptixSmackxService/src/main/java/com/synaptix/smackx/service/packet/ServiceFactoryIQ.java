package com.synaptix.smackx.service.packet;

import org.jivesoftware.smack.packet.IQ;

public abstract class ServiceFactoryIQ extends IQ {

	public ServiceFactoryIQ() {
		super();
	}

	protected abstract String toXMLExtension();

	public String getChildElementXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<query xmlns=\"synaptix.servicefactory:iq\">");
		sb.append(toXMLExtension());
		sb.append("</query>");
		return sb.toString();
	}

}
