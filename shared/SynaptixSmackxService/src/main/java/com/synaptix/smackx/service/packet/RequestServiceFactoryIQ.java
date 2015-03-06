package com.synaptix.smackx.service.packet;

import org.jivesoftware.smack.packet.IQ;

public class RequestServiceFactoryIQ extends ServiceFactoryIQ {

	private String serviceFactoryName;

	public RequestServiceFactoryIQ() {
		super();

		this.setType(IQ.Type.GET);
	}

	public String getServiceFactoryName() {
		return serviceFactoryName;
	}

	public void setServiceFactoryName(String serviceFactoryName) {
		this.serviceFactoryName = serviceFactoryName;
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<request xmlns=\"synaptix.servicefactory:iq\" ");
		sb.append("serviceFactoryName=\"");
		sb.append(serviceFactoryName);
		sb.append("\">");
		sb.append("</request>");
		return sb.toString();
	}
}
