package com.synaptix.smackx.version.packet;

import org.jivesoftware.smack.packet.IQ;

public class ResultServerVersionIQ extends ServerVersionIQ {

	private String serverVersion;

	public ResultServerVersionIQ() {
		super();

		this.setType(IQ.Type.RESULT);
	}

	public void setServerVersion(String serverVersion) {
		this.serverVersion = serverVersion;
	}

	public String getServerVersion() {
		return serverVersion;
	}

	protected String toXMLExtension() {
		StringBuilder sb = new StringBuilder();
		sb.append("<result xmlns=\"synaptix.version:iq\">");
		sb.append("<serverVersion xmlns=\"synaptix.version:iq\">");
		sb.append(serverVersion);
		sb.append("</serverVersion>");
		sb.append("</result>");
		return sb.toString();
	}
}
