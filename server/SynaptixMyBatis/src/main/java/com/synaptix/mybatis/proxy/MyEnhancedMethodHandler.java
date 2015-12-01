package com.synaptix.mybatis.proxy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.executor.loader.ResultLoaderMap;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyCopier;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.apache.ibatis.session.Configuration;

import javassist.util.proxy.MethodHandler;

class MyEnhancedMethodHandler implements MethodHandler {

	private static final String FINALIZE_METHOD = "finalize";

	private static final String WRITE_REPLACE_METHOD = "writeReplace";

	private Class<?> type;
	private ResultLoaderMap lazyLoader;
	private boolean aggressive;
	private Set<String> lazyLoadTriggerMethods;
	private ObjectFactory objectFactory;
	private List<Class<?>> constructorArgTypes;
	private List<Object> constructorArgs;

	public MyEnhancedMethodHandler(Class<?> type, ResultLoaderMap lazyLoader, Configuration configuration, ObjectFactory objectFactory, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
		this.type = type;
		this.lazyLoader = lazyLoader;
		this.aggressive = configuration.isAggressiveLazyLoading();
		this.lazyLoadTriggerMethods = configuration.getLazyLoadTriggerMethods();
		this.objectFactory = objectFactory;
		this.constructorArgTypes = constructorArgTypes;
		this.constructorArgs = constructorArgs;
	}

	public Object invoke(Object enhanced, Method method, Method methodProxy, Object[] args) throws Throwable {
		final String methodName = method.getName();
		try {
			synchronized (lazyLoader) {
				if (isWriteReplaceMethod(method)) {
					Object original = null;
					if (constructorArgTypes.isEmpty()) {
						original = objectFactory.create(type);
					} else {
						original = objectFactory.create(type, constructorArgTypes, constructorArgs);
					}
					lazyLoader.loadAll();
					PropertyCopier.copyBeanProperties(type, enhanced, original);
					return original;
				} else {
					if (lazyLoader.size() > 0 && !isFinalizeMethod(method)) {
						if (aggressive || lazyLoadTriggerMethods.contains(methodName) || enhanced instanceof Map) {
							lazyLoader.loadAll();
						} else if (PropertyNamer.isProperty(methodName)) {
							final String property = PropertyNamer.methodToProperty(methodName);
							if (lazyLoader.hasLoader(property)) {
								lazyLoader.load(property);
							}
						}
					}
				}
			}
			return methodProxy.invoke(enhanced, args);
		} catch (Throwable t) {
			throw ExceptionUtil.unwrapThrowable(t);
		}
	}

	private boolean isWriteReplaceMethod(Method method) {
		if (!Object.class.equals(method.getReturnType())) {
			return false;
		}
		if (method.getParameterTypes().length != 0) {
			return false;
		}
		String name = method.getName();
		if (name.equals(WRITE_REPLACE_METHOD)) {
			return true;
		}
		return false;
	}

	private boolean isFinalizeMethod(Method method) {
		if (!void.class.equals(method.getReturnType())) {
			return false;
		}
		if (method.getParameterTypes().length != 0) {
			return false;
		}
		String name = method.getName();
		if (name.equals(FINALIZE_METHOD)) {
			return true;
		}
		return false;
	}

}
