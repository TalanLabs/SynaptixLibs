package com.synaptix.server.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.service.AbstractServiceFactory;
import com.synaptix.service.NotFoundServiceException;

public class DefaultServerServiceFactory extends AbstractServiceFactory {

	private static final Log LOG = LogFactory.getLog(DefaultServerServiceFactory.class);

	private Map<Class<?>, ServiceDescriptor> serviceDescriptorMap;

	private Map<Class<?>, Object> serviceMap;

	public DefaultServerServiceFactory(String scope) {
		super(scope);

		serviceDescriptorMap = new HashMap<Class<?>, ServiceDescriptor>();
		serviceMap = new HashMap<Class<?>, Object>();
	}

	/**
	 * Add service
	 * 
	 * @param scope
	 * @param serviceClass
	 * @param serviceImpl
	 * @param description
	 */
	public void addService(Class<?> serviceClass, Object serviceImpl, String description) {
		if (serviceClass == null) {
			throw new IllegalArgumentException("serviceClass is null");
		}
		if (serviceImpl == null) {
			throw new IllegalArgumentException("serviceImpl is null");
		}
		if (!serviceClass.isInstance(serviceImpl)) {
			throw new IllegalArgumentException("serviceClass=" + serviceClass + " is not interface serviceImpl=" + serviceImpl);
		}
		LOG.info("Add service " + serviceClass.getName() + " in scope " + getScope());
		Object service2 = Proxy.newProxyInstance(DefaultServerServiceFactory.class.getClassLoader(), new Class<?>[] { serviceClass }, new MyInvocationHandler(serviceClass.getName(), serviceImpl));
		serviceDescriptorMap.put(serviceClass, new ServiceDescriptor(getScope(), serviceClass, description));
		serviceMap.put(serviceClass, service2);
	}

	@Override
	public ServiceDescriptor[] getServiceDescriptors() {
		Collection<ServiceDescriptor> serviceList = serviceDescriptorMap.values();
		return serviceList.toArray(new ServiceDescriptor[serviceList.size()]);
	}

	@Override
	public ServiceDescriptor getServiceDescriptor(Class<?> type) {
		return serviceDescriptorMap.get(type);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getService(Class<T> type) throws NotFoundServiceException {
		if (!serviceMap.containsKey(type)) {
			throw new NotFoundServiceException(type.getName() + " not exist"); //$NON-NLS-1$
		}
		return (T) serviceMap.get(type);
	}

	private final class MyInvocationHandler implements InvocationHandler {

		private String serviceName;

		private Object service;

		public MyInvocationHandler(String serviceName, Object service) {
			this.serviceName = serviceName;
			this.service = service;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			fireInterceptService(serviceName, method.getName(), method.getParameterTypes(), args);
			Object res = null;
			try {
				res = method.invoke(service, args);
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
			return res;
		}
	}
}
