package com.synaptix.client.service.packet;

import org.jivesoftware.smack.packet.IQ;

public class ResultInformationMessageIQ extends IQ {

	private String message;

	public ResultInformationMessageIQ() {
		super();

		this.setType(IQ.Type.ERROR);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<suicide>");
		sb.append("<informationMessage>");
		sb.append(message);
		sb.append("</informationMessage>");
		sb.append("</suicide>");
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
