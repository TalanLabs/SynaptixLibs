package com.synaptix.component.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentFactory;

public abstract class ComponentImpl extends PropertyChangeCapableImpl implements IComponent, Externalizable {

	private static final long serialVersionUID = -559250238071463681L;

	protected Map<String, Object> propertyValueMap;

	private transient static Map<Class<?>, Object> computedInstanceMap;

	static {
		computedInstanceMap = new HashMap<Class<?>, Object>();
	}

	public ComponentImpl() {
		super();
		propertyValueMap = new HashMap<String, Object>();
	}

	/**
	 * Put a computed instance for clazz
	 * 
	 * @param clazz
	 * @param instance
	 */
	public static final <E> void putComputedInstance(Class<E> clazz, E instance) {
		computedInstanceMap.put(clazz, instance);
	}

	/**
	 * Get a computed instance
	 * 
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <E> E getComputedInstance(Class<E> clazz) {
		return (E) computedInstanceMap.get(clazz);
	}

	protected <E> E createComputedInstance(Class<E> clazz) {
		return ComponentFactory.getInstance().getComputedFactory().createInstance(clazz);
	}

	@Override
	public Map<String, Object> straightGetProperties() {
		return new HashMap<String, Object>(propertyValueMap);
	}

	@Override
	public Object straightGetProperty(String propertyName) {
		return getter(propertyName);
	}

	@Override
	public void straightSetProperties(Map<String, Object> properties) {
		if (properties == null) {
			throw new IllegalArgumentException("properties is null");
		}
		for (Entry<String, Object> entry : properties.entrySet()) {
			setter(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void straightSetProperty(String propertyName, Object value) {
		setter(propertyName, value);
	}

	protected Object getter(String propertyName) {
		if (propertyName == null) {
			throw new IllegalArgumentException("propertyName is null");
		}
		if (!straightGetPropertyNames().contains(propertyName)) {
			throw new IllegalArgumentException("propertyName=" + propertyName + " is not property " + straightGetPropertyNames());
		}
		return propertyValueMap.get(propertyName);
	}

	protected void setter(String propertyName, Object value) {
		if (propertyName == null) {
			throw new IllegalArgumentException("propertyName is null");
		}
		if (!straightGetPropertyNames().contains(propertyName)) {
			throw new IllegalArgumentException("propertyName=" + propertyName + " is not property " + straightGetPropertyNames());
		}
		if (value == null && straightGetPropertyClass(propertyName).isPrimitive()) {
			throw new IllegalArgumentException("propertyName is primitive and value is null");
		}
		Object oldValue = propertyValueMap.get(propertyName);
		propertyValueMap.put(propertyName, value);
		firePropertyChange(propertyName, oldValue, value);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(propertyValueMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		propertyValueMap.clear();
		propertyValueMap.putAll((Map<String, Object>) in.readObject());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName()).append("@").append(super.hashCode()).append("(");
		boolean first = true;
		for (String propertyName : straightGetPropertyNames()) {
			if (!first) {
				sb.append(";");
			} else {
				first = false;
			}
			sb.append(propertyName).append("=").append(propertyValueMap.get(propertyName));
		}
		sb.append(")");
		return sb.toString();
	}
}
