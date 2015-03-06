package com.synaptix.client.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.service.AbstractServiceFactory;
import com.synaptix.service.NotFoundServiceException;

public class ClientServiceFactory extends AbstractServiceFactory {

	private static final Log LOG = LogFactory
			.getLog(ClientServiceFactory.class);

	private static Long defaultTimeout = 60000L;

	private static Map<Method, Long> methodMap = new HashMap<Method, Long>();

	private static Cache cache = new Cache();

	private IServiceCommunicator serviceCommunicator;

	private String serviceFactoryName;

	private Map<Class<?>, ServiceDescriptor> serviceDescriptorMap;

	private Map<Class<?>, Object> serviceMap;

	public ClientServiceFactory(IServiceCommunicator serviceCommunicator,
			String serviceFactoryName) {
		super(null);
		this.serviceCommunicator = serviceCommunicator;
		this.serviceFactoryName = serviceFactoryName;

		this.serviceDescriptorMap = new HashMap<Class<?>, ServiceDescriptor>();
		this.serviceMap = new HashMap<Class<?>, Object>();

		cache.putServiceFactoryName(serviceFactoryName);

		LOG.debug("Read service descriptors from " + serviceFactoryName); //$NON-NLS-1$
		try {
			List<IServiceCommunicator.ServiceDescriptor> serviceDescriptorList = serviceCommunicator
					.findAllServiceDescriptorListBy(serviceFactoryName);
			if (serviceDescriptorList != null
					&& !serviceDescriptorList.isEmpty()) {
				for (IServiceCommunicator.ServiceDescriptor sd : serviceDescriptorList) {
					LOG.debug(sd.getServiceClassName() + " Service found"); //$NON-NLS-1$
					ClassLoader classLoader = ClientServiceFactory.class
							.getClassLoader();
					try {
						Class<?> clazz = classLoader.loadClass(sd
								.getServiceClassName().trim());
						Object service = Proxy.newProxyInstance(
								ClientServiceFactory.class.getClassLoader(),
								new Class<?>[] { clazz },
								new MyInvocationHandler(clazz, sd
										.getServiceClassName()));

						ServiceDescriptor s = new ServiceDescriptor(
								sd.getScope(), clazz, sd.getDescription());
						serviceDescriptorMap.put(clazz, s);
						serviceMap.put(clazz, service);
						LOG.debug(sd.getServiceClassName() + " Service access"); //$NON-NLS-1$
					} catch (Exception e) {
						LOG.debug(sd.getServiceClassName()
								+ " Service not access", e); //$NON-NLS-1$
					}
				}
			}
		} catch (Exception e) {
			LOG.debug(e.getMessage(), e);
		}
	}

	public String getServiceFactoryName() {
		return serviceFactoryName;
	}

	@Override
	public ServiceDescriptor[] getServiceDescriptors() {
		Collection<ServiceDescriptor> serviceList = serviceDescriptorMap
				.values();
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

	public static synchronized void putTimeout(long timeout) {
		defaultTimeout = timeout;
	}

	/**
	 * Change default timeout for method
	 * 
	 * @param method
	 * @param timeout
	 *            en milliseconde
	 */
	public static synchronized void putTimeout(Method method, long timeout) {
		methodMap.put(method, timeout);
	}

	/**
	 * Change default timeout for service class
	 * 
	 * @param clazz
	 * @param timeout
	 *            en milliseconde
	 */
	public static void putTimeout(Class<?> clazz, long timeout) {
		if (clazz == null) {
			throw new NullPointerException("Class must not null"); //$NON-NLS-1$
		}
		putTimeout(clazz, null, false, null, timeout);
	}

	/**
	 * Change default timeout for all method name in service class
	 * 
	 * @param clazz
	 * @param methodName
	 * @param timeout
	 *            en milliseconde
	 */
	public static void putTimeout(Class<?> clazz, String methodName,
			long timeout) {
		if (clazz == null) {
			throw new NullPointerException("Class must not null"); //$NON-NLS-1$
		}
		if (methodName == null) {
			throw new NullPointerException("MethodName must not null"); //$NON-NLS-1$
		}
		putTimeout(clazz, methodName, true, null, timeout);
	}

	/**
	 * Change default timeout for a method name in service class
	 * 
	 * @param clazz
	 * @param methodName
	 * @param parameterTypes
	 * @param timeout
	 *            en milliseconde
	 */
	public static void putTimeout(Class<?> clazz, String methodName,
			Class<?>[] parameterTypes, long timeout) {
		if (clazz == null) {
			throw new NullPointerException("Class must not null"); //$NON-NLS-1$
		}
		if (methodName == null) {
			throw new NullPointerException("MethodName must not null"); //$NON-NLS-1$
		}
		putTimeout(clazz, methodName, false, parameterTypes, timeout);
	}

	private static void putTimeout(Class<?> clazz, String methodName,
			boolean allMethod, Class<?>[] parameterTypes, long timeout) {
		try {
			if (methodName == null) {
				Method[] methods = clazz.getMethods();
				if (methods != null) {
					for (Method method : methods) {
						putTimeout(method, timeout);
					}
				}
			} else if (allMethod) {
				Method[] methods = clazz.getMethods();
				for (Method method : methods) {
					if (method.getName().equals(methodName)) {
						putTimeout(method, timeout);
					}
				}
			} else {
				Method method = clazz.getMethod(methodName, parameterTypes);
				putTimeout(method, timeout);
			}
		} catch (SecurityException e) {
			LOG.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public static synchronized Long getTimeout(Method method) {
		return methodMap.get(method);
	}

	public static synchronized void putCacheData(
			final String serviceFactoryName, final Method method,
			final Object[] args, final Object result) {
		cache.putCacheData(serviceFactoryName, method, args, result);
	}

	public static void putCache(final String serviceFactoryName,
			final Class<?> clazz, final String methodName, final Object[] args,
			final Object result) {
		if (clazz == null) {
			throw new NullPointerException("Class must not null"); //$NON-NLS-1$
		}
		if (methodName == null) {
			throw new NullPointerException("MethodName must not null"); //$NON-NLS-1$
		}
		putCache(serviceFactoryName, clazz, methodName, null, result);
	}

	public static void putCache(final String serviceFactoryName,
			final Class<?> clazz, final Class<?>[] parameterTypes,
			final Object[] args, final Object result) {
		if (clazz == null) {
			throw new NullPointerException("Class must not null"); //$NON-NLS-1$
		}
		if (parameterTypes == null) {
			throw new NullPointerException("parameterTypes must not null"); //$NON-NLS-1$
		}
		putCache(serviceFactoryName, clazz, null, parameterTypes, args, result);
	}

	private static void putCache(final String serviceFactoryName,
			final Class<?> clazz, final String methodName,
			final Class<?>[] parameterTypes, final Object[] args,
			final Object result) {
		try {
			putCacheData(serviceFactoryName,
					MethodFinder.findMethod(clazz, methodName, parameterTypes),
					args, result);
		} catch (SecurityException e) {
			LOG.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public static synchronized Object getCache(final String serviceFactoryName,
			final Method method, final Object[] args) {
		return cache.getCacheData(serviceFactoryName, method, args);
	}

	public static Object flush(final String serviceFactoryName,
			final Class<?> clazz, final Class<?>[] parameterTypes) {
		return flush(serviceFactoryName, clazz, null, parameterTypes);
	}

	public static Object flush(final String serviceFactoryName,
			final Class<?> clazz, final String methodName) {
		return flush(serviceFactoryName, clazz, methodName, null);
	}

	public static Object flush(final String serviceFactoryName,
			final Class<?> clazz, final String methodName,
			final Class<?>[] parameterTypes) {
		try {
			return flushCache(serviceFactoryName,
					MethodFinder.findMethod(clazz, methodName, parameterTypes));
		} catch (SecurityException e) {
			LOG.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOG.error(e.getMessage(), e);
		}
		throw new IllegalArgumentException(
				"cache map does not contains any " + serviceFactoryName); //$NON-NLS-1$
	}

	public static synchronized Object flushCache(
			final String serviceFactoryName, final Method method) {
		return cache.flushCache(serviceFactoryName, method);
	}

	public static void setCacheDelay(final String serviceFactoryName,
			final Class<?> clazz, final Class<?>[] parameterTypes,
			final Integer delay) {
		setCacheDelay(serviceFactoryName, clazz, null, parameterTypes, delay);
	}

	public static void setCacheDelay(final String serviceFactoryName,
			final Class<?> clazz, final String methodName, final Integer delay) {
		setCacheDelay(serviceFactoryName, clazz, methodName, null, delay);
	}

	public static void setCacheDelay(final String serviceFactoryName,
			final Class<?> clazz, final String methodName,
			final Class<?>[] parameterTypes, final Integer delay) {
		try {
			setCacheDelay(serviceFactoryName,
					MethodFinder.findMethod(clazz, methodName, parameterTypes),
					delay);
		} catch (SecurityException e) {
			LOG.error(e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			LOG.error(e.getMessage(), e);
		}
		throw new IllegalArgumentException(
				"cache map does not contains any " + serviceFactoryName); //$NON-NLS-1$
	}

	public static synchronized void setCacheDelay(
			final String serviceFactoryName, final Method[] methods,
			final Integer delay) {
		if (methods != null && methods.length > 0) {
			for (Method method : methods) {
				cache.putCacheDelay(serviceFactoryName, method, delay);
			}
		}
	}

	public static synchronized void setCacheDelay(
			final String serviceFactoryName, final Method method,
			final Integer delay) {
		cache.putCacheDelay(serviceFactoryName, method, delay);
	}

	public static synchronized void flushAll(final String serviceFactoryName) {
		cache.flushAllCache(serviceFactoryName);
	}

	public static synchronized void flushAllCache() {
		cache.flushAllCache();
	}

	private final class MyInvocationHandler implements InvocationHandler {

		private final Class<?> serviceClass;

		private final String serviceName;

		public MyInvocationHandler(Class<?> serviceClass, String serviceName) {
			super();

			this.serviceClass = serviceClass;
			this.serviceName = serviceName;
		}

		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			fireInterceptService(serviceName, method.getName(),
					method.getParameterTypes(), args);

			Long timeout = null;
			synchronized (methodMap) {
				timeout = methodMap.get(method);
			}
			if (timeout == null) {
				timeout = defaultTimeout;
			}

			Object res = null;
			try {
				final Object cac = getCache(serviceFactoryName, method, args);
				if (cac != null) {
					res = ClientServiceFactory.clone(cac);
				} else {
					res = serviceCommunicator.sendMessage(serviceFactoryName,
							serviceName, method.getName(),
							method.getParameterTypes(), args, timeout);
					putCacheData(serviceFactoryName, method, args, res);
				}
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
			return res;
		}
	}

	private static Object clone(final Object obj) {
		Object clone = null;
		ObjectOutputStream objOs = null;
		ObjectInputStream objIs = null;
		try {
			final ByteArrayOutputStream byteArrOs = new ByteArrayOutputStream();
			objOs = new ObjectOutputStream(byteArrOs);
			objOs.writeObject(obj);
			final ByteArrayInputStream byteArrIs = new ByteArrayInputStream(
					byteArrOs.toByteArray());
			objIs = new ObjectInputStream(byteArrIs);
			clone = objIs.readObject();
		} catch (final Exception e) {
			clone = obj;
		} finally {
			if (objOs != null) {
				try {
					objOs.close();
				} catch (final IOException e) {
					LOG.error(
							"ClientServiceFactory.clone.ObjectOutputStream.close",
							e);
				}
			}
			if (objIs != null) {
				try {
					objIs.close();
				} catch (final IOException e) {
					LOG.error(
							"ClientServiceFactory.clone.ObjectInputStream.close",
							e);
				}
			}
		}
		return clone;
	}
}
