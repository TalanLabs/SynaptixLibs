package com.synaptix.smackx.service;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

import com.synaptix.smackx.service.packet.RequestServiceCallbackIQ;

public class ServiceCallbackPacketListener implements PacketListener {

	private static final Log log = LogFactory.getLog(ServiceCallbackPacketListener.class);

	private Class<?>[] argTypes;

	private Object[] args;

	public ServiceCallbackPacketListener(Class<?>[] argTypes, Object[] args) {
		super();

		this.args = args;
		this.argTypes = argTypes;
	}

	public void processPacket(Packet packet) {
		RequestServiceCallbackIQ rsciq = (RequestServiceCallbackIQ) packet;

		log.debug("Recveid service callback currentID : " + rsciq.getPacketID() + " positionArg " + rsciq.getPositionArg() + " method " + rsciq.getMethodName());

		Class<?> clazz = argTypes[rsciq.getPositionArg()];
		Object value = args[rsciq.getPositionArg()];
		try {
			Method subMethod = clazz.getMethod(rsciq.getMethodName(), rsciq.getArgTypes());
			subMethod.invoke(value, rsciq.getArgs());
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}
}