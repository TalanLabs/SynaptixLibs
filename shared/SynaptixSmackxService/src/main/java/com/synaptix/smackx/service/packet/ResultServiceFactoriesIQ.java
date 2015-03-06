package com.synaptix.smackx.service.packet;

import org.jivesoftware.smack.packet.IQ;

public class ResultServiceFactoriesIQ extends ServiceFactoryIQ {

	private String[] serviceFactoryNames;

	public ResultServiceFactoriesIQ() {
		super();

		this.setType(IQ.Type.RESULT);
	}

	public String[] getServiceFactoryNames() {
		return serviceFactoryNames;
	}

	public void setServiceFactoryNames(String[] serviceFactoryNames) {
		this.serviceFactoryNames = serviceFactoryNames;
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<result xmlns=\"synaptix.servicefactory:iq\">");
		sb.append("<servicefactories>");
		for (String name : serviceFactoryNames) {
			sb.append("<servicefactory name=\"");
			sb.append(name);
			sb.append("\"/>");
		}
		sb.append("</servicefactories>");
		sb.append("</result>");
		return sb.toString();
	}
}
