package com.synaptix.server.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javassist.util.proxy.MethodHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.synaptix.service.AbstractServiceFactory;
import com.synaptix.service.NotFoundServiceException;

public class GuiceServerServiceFactory extends AbstractServiceFactory {

	private static final Log LOG = LogFactory.getLog(GuiceServerServiceFactory.class);

	private final Injector injector;

	private Map<Class<?>, ServiceDescriptor> serviceDescriptorMap;

	private Map<Class<?>, Class<?>> serviceMap;

	private Map<Class<?>, Object> implServiceMap;

	@Inject
	public GuiceServerServiceFactory(Injector injector) {
		super("");
		this.injector = injector;

		serviceDescriptorMap = new HashMap<Class<?>, ServiceDescriptor>();
		serviceMap = new HashMap<Class<?>, Class<?>>();
		implServiceMap = new HashMap<Class<?>, Object>();
	}

	public <E, F extends E> void addService(Class<E> serviceClass, Class<F> implServiceClass) {
		if (serviceClass == null) {
			throw new IllegalArgumentException("serviceClass is null");
		}
		if (implServiceClass == null) {
			throw new IllegalArgumentException("serviceImpl is null");
		}
		LOG.info("Add service " + serviceClass.getName() + " to " + implServiceClass.getName());
		serviceDescriptorMap.put(serviceClass, new ServiceDescriptor(getScope(), serviceClass, ""));
		serviceMap.put(serviceClass, implServiceClass);
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
		if (!implServiceMap.containsKey(type)) {
			if (!serviceMap.containsKey(type)) {
				throw new NotFoundServiceException(type.getName() + " not exist"); //$NON-NLS-1$	
			}
			Object serviceImpl = injector.getInstance(serviceMap.get(type));
			implServiceMap.put(type, serviceImpl);

			// ProxyFactory enhancer = new ProxyFactory();
			// enhancer.setSuperclass(serviceImpl.getClass());
			// enhancer.setInterfaces(new Class[] { type });
			//
			// try {
			// Object service2 = enhancer.create(new Class<?>[0], new Object[0], new MyMethodHandler(type.getName(), serviceImpl));
			// implServiceMap.put(type, service2);
			// } catch (Exception e) {
			// throw new RuntimeException(e);
			// }
			// Object service2 = Proxy.newProxyInstance(GuiceServerServiceFactory.class.getClassLoader(), new Class<?>[] { type }, new MyInvocationHandler(type.getName(), serviceImpl));
			// implServiceMap.put(type, service2);
		}
		return (T) implServiceMap.get(type);
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

	private final class MyMethodHandler implements MethodHandler {

		private String serviceName;

		private Object service;

		public MyMethodHandler(String serviceName, Object service) {
			this.serviceName = serviceName;
			this.service = service;
		}

		@Override
		public Object invoke(Object enhanced, Method method, Method methodProxy, Object[] args) throws Throwable {
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
