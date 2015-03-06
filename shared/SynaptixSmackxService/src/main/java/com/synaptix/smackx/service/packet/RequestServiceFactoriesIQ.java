package com.synaptix.smackx.service.packet;

import org.jivesoftware.smack.packet.IQ;

public class RequestServiceFactoriesIQ extends ServiceFactoryIQ {

	public RequestServiceFactoriesIQ() {
		super();

		this.setType(IQ.Type.GET);
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<request xmlns=\"synaptix.servicefactory:iq\">");
		sb.append("</request>");
		return sb.toString();
	}
}
