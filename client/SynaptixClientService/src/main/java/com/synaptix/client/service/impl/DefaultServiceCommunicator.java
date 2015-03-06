package com.synaptix.client.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.MethodUtils;

import com.synaptix.client.service.IServiceCommunicator;

public class DefaultServiceCommunicator implements IServiceCommunicator {

	private Map<String, List<ServiceDescriptor>> serviceDescriptorsMap = new HashMap<String, List<IServiceCommunicator.ServiceDescriptor>>();

	private Map<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();

	public <E, F extends E> void addService(String serviceFactoryName, Class<E> interfaceService, F implService) {
		List<ServiceDescriptor> list = serviceDescriptorsMap.get(serviceFactoryName);
		if (list == null) {
			list = new ArrayList<IServiceCommunicator.ServiceDescriptor>();
			serviceDescriptorsMap.put(serviceFactoryName, list);
		}
		ServiceDescriptor sd = new ServiceDescriptor("", interfaceService.getName(), "");
		list.add(sd);

		Map<String, Object> m = map.get(serviceFactoryName);
		if (m == null) {
			m = new HashMap<String, Object>();
			map.put(serviceFactoryName, m);
		}
		m.put(interfaceService.getName(), implService);
	}

	@Override
	public List<ServiceDescriptor> findAllServiceDescriptorListBy(String serviceFactoryName) throws Exception {
		return serviceDescriptorsMap.get(serviceFactoryName);
	}

	public Object sendMessage(String serviceFactoryName, String serviceName, String methodName, Class<?>[] parameterTypes, Object[] args, Long timeout) throws Exception {
		Map<String, Object> m = map.get(serviceFactoryName);
		if (m == null) {
			throw new IllegalStateException("Not existe service for serviceFactoryName " + serviceFactoryName);
		}
		Object o = m.get(serviceName);
		if (o == null) {
			throw new IllegalStateException("Not existe service for serviceFactoryName " + serviceFactoryName);
		}
		return MethodUtils.invokeExactMethod(o, methodName, args, parameterTypes);
	}
}
