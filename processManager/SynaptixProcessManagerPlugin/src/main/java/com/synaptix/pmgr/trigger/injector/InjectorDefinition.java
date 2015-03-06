package com.synaptix.pmgr.trigger.injector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class InjectorDefinition {
	private String id;
	private String[] parameters;
	private String desc;
	private Class<?> cls;
	MessageInjector resolvedInjector;

	public InjectorDefinition(String id, String desc, String[] parameters, String clsname, ClassLoader classLoader) throws ClassNotFoundException {
		this.id = id;
		this.desc = desc;
		this.parameters = parameters;
		this.cls = classLoader.loadClass(clsname);
	}

	public String getID() {
		return id;
	}

	public String getDesc() {
		return desc;
	}

	public Class<?> getAgentClass() {
		return cls;
	}

	public boolean isResolved() {
		return resolvedInjector != null;
	}

	public MessageInjector resolveInjector() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		if (resolvedInjector == null) {
			Constructor<?> constructor = null;
			if ((parameters != null) && (parameters.length > 0)) {
				Class<?>[] sig = new Class[parameters.length];
				for (int i = 0; i < sig.length; i++) {
					sig[i] = String.class;
				}
				constructor = cls.getConstructor(sig);
			}
			if ((parameters != null) && (parameters.length > 0)) {
				resolvedInjector = (MessageInjector) constructor.newInstance(parameters);
			} else {
				resolvedInjector = (MessageInjector) cls.newInstance();
			}

		}
		return resolvedInjector;
	}
}
