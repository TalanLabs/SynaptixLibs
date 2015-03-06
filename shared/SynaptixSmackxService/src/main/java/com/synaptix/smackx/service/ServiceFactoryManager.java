package com.synaptix.smackx.service;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;

import com.synaptix.smackx.service.packet.RequestServiceFactoriesIQ;
import com.synaptix.smackx.service.packet.RequestServiceFactoryIQ;
import com.synaptix.smackx.service.packet.ResultServiceFactoriesIQ;
import com.synaptix.smackx.service.packet.ResultServiceFactoryIQ;

public class ServiceFactoryManager {

	public static String[] getServiceFactoryNames(XMPPConnection connection)
			throws XMPPException {
		RequestServiceFactoriesIQ iq = new RequestServiceFactoriesIQ();

		PacketCollector collector = connection
				.createPacketCollector(new PacketIDFilter(iq.getPacketID()));

		connection.sendPacket(iq);

		IQ result = (IQ) collector.nextResult();
		if (result == null) {
			throw new XMPPException("No response from the server.");
		}
		if (result.getType() == IQ.Type.ERROR) {
			throw new XMPPException(result.getError());
		}
		return ((ResultServiceFactoriesIQ) result).getServiceFactoryNames();
	}

	public static ResultServiceFactoryIQ.ServiceDescriptor[] getServiceDescriptors(
			XMPPConnection connection, String serviceFactoryName)
			throws XMPPException {
		RequestServiceFactoryIQ iq = new RequestServiceFactoryIQ();
		iq.setServiceFactoryName(serviceFactoryName);

		PacketCollector collector = connection
				.createPacketCollector(new PacketIDFilter(iq.getPacketID()));

		connection.sendPacket(iq);

		IQ result = (IQ) collector.nextResult();
		if (result == null) {
			throw new XMPPException("No response from the server.");
		}
		if (result.getType() == IQ.Type.ERROR) {
			throw new XMPPException(result.getError());
		}
		return ((ResultServiceFactoryIQ) result).getServiceDescriptors();
	}

}
