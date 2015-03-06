package com.synaptix.mybatis.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Properties;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.loader.ResultLoaderMap;
import org.apache.ibatis.executor.loader.WriteReplaceInterface;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyCopier;
import org.apache.ibatis.session.Configuration;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentFactory;

public class ComponentProxyFactory implements org.apache.ibatis.executor.loader.ProxyFactory {

	private static final Log LOG = LogFactory.getLog(ComponentProxyFactory.class);

	private static final String WRITE_REPLACE_METHOD = "writeReplace";

	public ComponentProxyFactory() {
		super();
	}

	@Override
	public Object createProxy(Object target, ResultLoaderMap lazyLoader, Configuration configuration, ObjectFactory objectFactory, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
		if (target instanceof IComponent) {
			if (ComponentFactory.getInstance().getComponentClass((IComponent) target) != null) {
				return _createProxy((IComponent) target, lazyLoader, configuration, objectFactory, constructorArgTypes, constructorArgs);
			}
		}
		return _createProxy(target, lazyLoader, configuration, objectFactory, constructorArgTypes, constructorArgs);
	}

	@Override
	public void setProperties(Properties properties) {
	}

	@SuppressWarnings("unchecked")
	private <E extends IComponent> Object _createProxy(E component, ResultLoaderMap lazyLoader, Configuration configuration, ObjectFactory objectFactory, List<Class<?>> constructorArgTypes,
			List<Object> constructorArgs) {
		Class<E> componentClass = ComponentFactory.getInstance().getComponentClass(component);
		ClassLoader classLoader = componentClass.getClassLoader();
		Class<?>[] interfaces = new Class[] { componentClass, WriteReplaceInterface.class, com.synaptix.component.factory.Proxy.class };
		InvocationHandler proxy = new MyComponentMethodHandler<E>(componentClass, component, lazyLoader, configuration);
		return (E) Proxy.newProxyInstance(classLoader, interfaces, proxy);
	}

	private Object _createProxy(Object target, ResultLoaderMap lazyLoader, Configuration configuration, ObjectFactory objectFactory, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
		final Class<?> type = target.getClass();
		MyEnhancedMethodHandler callback = new MyEnhancedMethodHandler(type, lazyLoader, configuration, objectFactory, constructorArgTypes, constructorArgs);
		Object enhanced = crateProxy(type, callback, constructorArgTypes, constructorArgs);
		PropertyCopier.copyBeanProperties(type, target, enhanced);
		return enhanced;
	}

	private static Object crateProxy(Class<?> type, MethodHandler callback, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
		ProxyFactory enhancer = new ProxyFactory();
		enhancer.setSuperclass(type);
		try {
			type.getDeclaredMethod(WRITE_REPLACE_METHOD);
			// ObjectOutputStream will call writeReplace of objects returned by
			// writeReplace
			LOG.debug(WRITE_REPLACE_METHOD + " method was found on bean " + type + ", make sure it returns this");
		} catch (NoSuchMethodException e) {
			enhancer.setInterfaces(new Class[] { WriteReplaceInterface.class });
		} catch (SecurityException e) {
			// nothing to do here
		}

		Object enhanced = null;
		Class<?>[] typesArray = constructorArgTypes.toArray(new Class[constructorArgTypes.size()]);
		Object[] valuesArray = constructorArgs.toArray(new Object[constructorArgs.size()]);
		try {
			enhanced = enhancer.create(typesArray, valuesArray, callback);
		} catch (Exception e) {
			throw new ExecutorException("Error creating lazy proxy.  Cause: " + e, e);
		}
		return enhanced;
	}
}
