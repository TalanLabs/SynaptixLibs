package com.synaptix.jmeter.plugin.sampler.packet;

import org.jivesoftware.smack.packet.IQ;

public class ResultJMeterServiceIQ extends IQ {

	private String result;

	public ResultJMeterServiceIQ() {
		super();

		this.setType(IQ.Type.RESULT);
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<result xmlns=\"synaptix.service:iq\">");
		sb.append(result);
		sb.append("</result>");
		return sb.toString();
	}

	public String getChildElementXML() {
		StringBuilder sb = new StringBuilder();
		sb.append("<query xmlns=\"synaptix.service:iq\">");
		sb.append(toXMLExtension());
		sb.append("</query>");
		return sb.toString();
	}

}
