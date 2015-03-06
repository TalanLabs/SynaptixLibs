package com.synaptix.jmeter.plugin.sampler.packet;

import org.jivesoftware.smack.packet.IQ;

public class RequestJMeterServiceIQ extends IQ {

	private final String childExtensionXML;

	public RequestJMeterServiceIQ(String childExtensionXML) {
		super();

		this.childExtensionXML = childExtensionXML;
	}

	public String getChildElementXML() {
		return childExtensionXML;
	}
}
