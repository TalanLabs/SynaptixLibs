package com.synaptix.gwt.shared.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class ComponentDtoImpl extends PropertyChangeCapableDtoImpl implements IComponentDto {

	private static final long serialVersionUID = -1730340075394053954L;

	protected Map<String, Object> propertyValueMap;

	private transient static Map<Class<?>, Object> computedInstanceMap;

	static {
		computedInstanceMap = new HashMap<Class<?>, Object>();
	}

	public ComponentDtoImpl() {
		super();
		propertyValueMap = new HashMap<String, Object>();
	}

	/**
	 * Put a computed instance for clazz
	 * 
	 * @param clazz
	 * @param instance
	 */
	public static final void putComputedInstance(Class<?> clazz, Object instance) {
		computedInstanceMap.put(clazz, instance);
	}

	/**
	 * Get a computed instance
	 * 
	 * @param clazz
	 * @return
	 */
	public static final Object getComputedInstance(Class<?> clazz) {
		return computedInstanceMap.get(clazz);
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
		Object oldValue = propertyValueMap.get(propertyName);
		propertyValueMap.put(propertyName, value);
		firePropertyChange(propertyName, oldValue, value);
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
