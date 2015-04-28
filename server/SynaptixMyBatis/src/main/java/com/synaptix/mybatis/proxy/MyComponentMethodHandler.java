package com.synaptix.mybatis.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Set;

import javassist.util.proxy.MethodHandler;

import org.apache.ibatis.executor.loader.ResultLoaderMap;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.Configuration;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentBeanMethod;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;

class MyComponentMethodHandler<E extends IComponent> implements MethodHandler, InvocationHandler {

	private static final String FINALIZE_METHOD = "finalize";

	private static final String WRITE_REPLACE_METHOD = "writeReplace";

	private final Class<E> componentClass;

	private final ComponentDescriptor componentDescriptor;

	private final E component;

	private final ResultLoaderMap lazyLoader;

	private final boolean aggressive;

	public MyComponentMethodHandler(Class<E> componentClass, E component, ResultLoaderMap lazyLoader, Configuration configuration) {
		super();
		this.componentClass = componentClass;
		this.componentDescriptor = ComponentFactory.getInstance().getDescriptor(componentClass);
		this.component = component;
		this.lazyLoader = lazyLoader;
		this.aggressive = configuration.isAggressiveLazyLoading();
	}

	@Override
	public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
		return invoke(self, thisMethod, args);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			Object res;
			synchronized (lazyLoader) {
				if (isWriteReplaceMethod(method)) {
					lazyLoader.loadAll();
					res = component;
				} else {
					if (lazyLoader.size() > 0 && !isFinalizeMethod(method)) {
						if (aggressive) {
							lazyLoader.loadAll();
						} else {
							ComponentBeanMethod cbm = componentDescriptor.getComponentBeanMethod(method.toGenericString());
							if (cbm != null) {
								switch (cbm) {
								case COMPUTED_GET:
								case COMPUTED_SET:
									// lazyLoader.loadAll(); // devrait fonctionner sans lazy loading, la méthode computed concernée va charger les objets nécessaires
									break;
								case GET:
								case SET: {
									String propertyName = cbm.inferName(method);
									if (lazyLoader.hasLoader(propertyName)) {
										lazyLoader.load(propertyName);
									}
								}
									break;
								case STRAIGHT_GET_PROPERTY:
								case STRAIGHT_SET_PROPERTY: {
									String propertyName = (String) args[0];
									if (lazyLoader.hasLoader(propertyName)) {
										lazyLoader.load(propertyName);
									}
								}
									break;
								case EQUALS:
								case HASHCODE:
									Set<String> propertyNames = componentDescriptor.getEqualsKeyPropertyNames();
									if (propertyNames != null && !propertyNames.isEmpty()) {
										for (String propertyName : propertyNames) {
											if (lazyLoader.hasLoader(propertyName)) {
												lazyLoader.load(propertyName);
											}
										}
									}
									break;
								case TO_STRING:
								case STRAIGHT_GET_PROPERTIES:
								case STRAIGHT_SET_PROPERTIES:
									lazyLoader.loadAll();
									break;
								default:
									break;
								}
							}
						}
					}

					res = method.invoke(component, args);
				}
			}
			return res;
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
