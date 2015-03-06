package com.synaptix.client.service.packet;

import org.jivesoftware.smack.packet.IQ;

public class ResultSuicideIQ extends IQ {

	private boolean force;

	public ResultSuicideIQ() {
		super();

		this.setType(IQ.Type.ERROR);
	}

	public boolean isForce() {
		return force;
	}

	public void setForce(boolean force) {
		this.force = force;
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<suicide>");
		sb.append("<force>");
		sb.append(isForce());
		sb.append("</force>");
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
