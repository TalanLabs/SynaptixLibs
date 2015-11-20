package com.synaptix.component.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.component.IComponent;
import com.synaptix.component.IComputedFactory;
import com.synaptix.component.extension.IComponentExtensionProcessor;

/**
 * ComponentFactory : create a component instance
 * 
 * @author Gaby
 * 
 */
public class ComponentFactory {

	private static Log LOG = LogFactory.getLog(ComponentFactory.class);

	private static ComponentFactory instance;

	public static ComponentFactory getInstance() {
		if (instance == null) {
			instance = new ComponentFactory();
		}
		return instance;
	}

	private Map<Class<? extends IComponent>, ComponentDescriptor> descriptorMap;

	private Map<Class<? extends IComponent>, Class<? extends IComponent>> implMap;

	private Map<Class<?>, IComponentExtensionProcessor> componentExtensionProcessorMap;

	private IComputedFactory computedFactory;

	private List<IComponentFactoryListener> componentFactoryListeners;

	public ComponentFactory() {
		super();

		this.descriptorMap = new HashMap<Class<? extends IComponent>, ComponentDescriptor>();
		this.implMap = new HashMap<Class<? extends IComponent>, Class<? extends IComponent>>();
		this.componentExtensionProcessorMap = new HashMap<Class<?>, IComponentExtensionProcessor>();
		this.computedFactory = new DefaultComputedFactory();
		this.componentFactoryListeners = new ArrayList<IComponentFactoryListener>();
	}

	/**
	 * Add componentFactoryListener
	 */
	public void addComponentFactoryListener(IComponentFactoryListener l) {
		componentFactoryListeners.add(l);
	}

	/**
	 * Remove componentFactoryListener
	 */
	public void removeComponentFactoryListener(IComponentFactoryListener l) {
		componentFactoryListeners.remove(l);
	}

	/*
	 * Fire after created instance
	 */
	protected <G extends IComponent> void fireAfterCreated(Class<G> interfaceClass, G instance) {
		for (IComponentFactoryListener componentFactoryListener : componentFactoryListeners) {
			componentFactoryListener.afterCreated(interfaceClass, instance);
		}
	}

	/**
	 * Désactivé pour le moment
	 * 
	 * Create a instance of interfaceClass with impl
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	private <G extends IComponent> G _createInstance(Class<G> interfaceClass) {
		if (interfaceClass == null) {
			throw new NullArgumentException("interfaceClass is null");
		}
		G res;
		Class<G> clazz = (Class<G>) implMap.get(interfaceClass);
		if (clazz == null) {
			String packageName = interfaceClass.getPackage() != null ? interfaceClass.getPackage().getName() : null;
			String simpleName = rename(interfaceClass.getSimpleName()) + "Impl";
			try {
				clazz = (Class<G>) interfaceClass.getClassLoader().loadClass(packageName != null ? packageName + "." + simpleName : simpleName);
				implMap.put(interfaceClass, clazz);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		try {
			res = clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return res;
	}

	private static String rename(String name) {
		String res = name;
		if (name.length() > 2) {
			String second = name.substring(1, 2);
			boolean startI = name.substring(0, 1).equals("I") && second.toUpperCase().equals(second);
			if (startI) {
				res = name.substring(1);
			}
		}
		return res;
	}

	/**
	 * Create a instance of interfaceClass with proxy class
	 */
	@SuppressWarnings("unchecked")
	public <G extends IComponent> G createInstance(Class<G> interfaceClass) {
		ClassLoader classLoader = interfaceClass.getClassLoader();
		Class<?>[] interfaces = new Class[] { interfaceClass, com.synaptix.component.factory.Proxy.class };
		InvocationHandler proxy = newProxy(interfaceClass);
		G instance = (G) Proxy.newProxyInstance(classLoader, interfaces, proxy);
		fireAfterCreated(interfaceClass, instance);
		return instance;
	}

	/**
	 * Get descriptor of interfaceClass
	 */
	public ComponentDescriptor getDescriptor(Class<? extends IComponent> componentClass) {
		if (componentClass == null) {
			throw new NullArgumentException("componentClass is null");
		}
		ComponentDescriptor res = descriptorMap.get(componentClass);
		if (res == null) {
			res = newDescriptor(componentClass);
			descriptorMap.put(componentClass, res);
		}
		return res;
	}

	/**
	 * Get a descriptor of instance
	 */
	public <G extends IComponent> ComponentDescriptor getDescriptor(G instance) {
		if (instance == null) {
			throw new NullArgumentException("instance is null");
		}
		return getDescriptor(getComponentClass(instance));
	}

	/**
	 * Test if clazz is a assignable a IComponent
	 */
	public static final boolean isClass(Class<?> clazz) {
		if (clazz == null) {
			throw new NullArgumentException("clazz is null");
		}
		return IComponent.class.isAssignableFrom(clazz);
	}

	private <G extends IComponent> ComponentDescriptor newDescriptor(Class<G> interfaceClass) {
		return new ComponentDescriptor(interfaceClass);
	}

	private <G extends IComponent> InvocationHandler newProxy(Class<G> interfaceClass) {
		return new ComponentProxy(interfaceClass);
	}

	/**
	 * Add extension
	 */
	public void addExtension(Class<?> componentExtensionClass, IComponentExtensionProcessor componentExtensionProcessor) {
		if (componentExtensionClass == null) {
			throw new NullArgumentException("componentExtensionClass is null");
		}
		if (componentExtensionProcessor == null) {
			throw new NullArgumentException("componentExtensionProcessor is null");
		}
		if (!componentExtensionClass.isInterface()) {
			throw new IllegalArgumentException(componentExtensionClass + "must be a interface class");
		}
		if (!componentExtensionProcessorMap.containsKey(componentExtensionClass)) {
			componentExtensionProcessorMap.put(componentExtensionClass, componentExtensionProcessor);
		} else {
			LOG.warn(componentExtensionClass + " already added");
		}
	}

	/**
	 * Get a component extension processor
	 */
	public IComponentExtensionProcessor getExtension(Class<?> componentExtensionClass) {
		if (componentExtensionClass == null) {
			throw new NullArgumentException("componentExtensionClass is null");
		}
		return componentExtensionProcessorMap.get(componentExtensionClass);
	}

	/**
	 * Get all extension class
	 */
	public Set<Class<?>> getExtensionClasss() {
		return Collections.unmodifiableSet(componentExtensionProcessorMap.keySet());
	}

	/**
	 * Set computed instanciator
	 */
	public void setComputedFactory(IComputedFactory computedFactory) {
		if (computedFactory == null) {
			throw new NullArgumentException("computedFactory is null");
		}
		this.computedFactory = computedFactory;
	}

	/**
	 * Get computed instanciator
	 */
	public IComputedFactory getComputedFactory() {
		return computedFactory;
	}

	@SuppressWarnings("unchecked")
	/**
	 * Get Component class for instance
	 * @param instance
	 * @return
	 */
	public final <E extends IComponent> Class<E> getComponentClass(E instance) {
		if (instance instanceof com.synaptix.component.factory.Proxy) {
			return (Class<E>) (((com.synaptix.component.factory.Proxy) instance).straightGetComponentClass());
		}
		return null;
	}
}
