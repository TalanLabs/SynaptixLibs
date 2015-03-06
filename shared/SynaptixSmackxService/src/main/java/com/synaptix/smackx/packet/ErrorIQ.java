package com.synaptix.smackx.packet;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.XMPPError;

public class ErrorIQ extends IQ {

	public ErrorIQ(XMPPError error) {
		this.setType(Type.ERROR);
		setError(error);
	}

	public String getChildElementXML() {
		return null;
	}
}