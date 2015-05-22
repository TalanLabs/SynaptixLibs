package com.synaptix.smackx.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.XMPPConnection;

import com.synaptix.client.service.IReWaitResult;
import com.synaptix.client.service.IServiceCommunicator;
import com.synaptix.client.service.IServiceMessageRecveidListener;
import com.synaptix.smackx.service.packet.ResultServiceFactoryIQ;

public class XmppServiceCommunicator implements IServiceCommunicator {

	private final XmppConnectionInformation xmppConnectionInformation;

	private final XMPPConnection xmppConnection;

	public XmppServiceCommunicator(XMPPConnection xmppConnection) {
		super();

		this.xmppConnectionInformation = new XmppConnectionInformation();
		this.xmppConnection = xmppConnection;
	}

	public void setDefaultReWaitResult(IReWaitResult reWaitResult) {
		this.xmppConnectionInformation.setDefaultReWaitResult(reWaitResult);
	}

	public void addServiceMessageRecveidListener(IServiceMessageRecveidListener l) {
		xmppConnectionInformation.addServiceMessageRecveidListener(l);
	}

	public void removeServiceMessageRecveidListener(IServiceMessageRecveidListener l) {
		xmppConnectionInformation.removeServiceMessageRecveidListener(l);
	}

	public List<String> findAllServiceFactoryNameList() throws Exception {
		return Arrays.asList(ServiceFactoryManager.getServiceFactoryNames(xmppConnection));
	}

	@Override
	public List<ServiceDescriptor> findAllServiceDescriptorListBy(String serviceFactoryName) throws Exception {
		List<ServiceDescriptor> serviceDescriptorList = new ArrayList<ServiceDescriptor>();
		ResultServiceFactoryIQ.ServiceDescriptor[] sds = ServiceFactoryManager.getServiceDescriptors(xmppConnection, serviceFactoryName);
		if (sds != null && sds.length > 0) {
			for (ResultServiceFactoryIQ.ServiceDescriptor sd : sds) {
				ServiceDescriptor s = new ServiceDescriptor(sd.getScope(), sd.getInterfaceName(), sd.getDescription());
				serviceDescriptorList.add(s);
			}
		}
		return serviceDescriptorList;
	}

	@Override
	public Object sendMessage(String serviceFactoryName, String serviceName, String methodName, Class<?>[] parameterTypes, Object[] args, Long timeout) throws Exception {
		return SendXmppServiceManager.getInstance(xmppConnection, xmppConnectionInformation).sendMessage(serviceFactoryName, serviceName, methodName, parameterTypes, args, timeout);
	}
}
