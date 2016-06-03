package com.synaptix.component.factory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor.ComputedMethodDescriptor;

class ComponentProxy implements InvocationHandler, Serializable {

	private static final ObjectStreamField[] serialPersistentFields = {new ObjectStreamField("componentClassName", String.class), new ObjectStreamField("propertyValueMap", Map.class)};
	private static final long serialVersionUID = -1210441501657033411L;
	private static Log LOG = LogFactory.getLog(ComponentProxy.class);
	protected Class<? extends IComponent> componentClass;

	protected String componentClassName;
	protected Map<String, Object> propertyValueMap;
	private transient ComponentDescriptor componentDescriptor;
	private transient List<PropertyChangeListener> listeners;
	private transient Map<String, List<PropertyChangeListener>> listenerMap;
	private transient Map<Class<?>, Object> computeInstanceMap;

	@SuppressWarnings("unchecked")
	ComponentProxy(String componentClassName) throws ClassNotFoundException {
		super();

		this.componentClass = (Class<? extends IComponent>) Class.forName(componentClassName);
		this.componentClassName = componentClassName;

		initialize();
	}

	ComponentProxy(Class<? extends IComponent> componentClass) {
		super();

		this.componentClass = componentClass;
		this.componentClassName = componentClass.getName();

		initialize();
	}

	private void initialize() {
		propertyValueMap = new HashMap<String, Object>();
		for (String propertyName : getDescriptor().getPropertyNames()) {
			propertyValueMap.put(propertyName, PrimitiveHelper.determineValue(getDescriptor().getPropertyClass(propertyName)));
		}
	}

	protected ComponentDescriptor getDescriptor() {
		if (componentDescriptor == null) {
			componentDescriptor = ComponentFactory.getInstance().getDescriptor(componentClass);
		}
		return componentDescriptor;
	}

	private List<PropertyChangeListener> getListeners() {
		if (listeners == null) {
			listeners = new ArrayList<PropertyChangeListener>();
		}
		return listeners;
	}

	private Map<String, List<PropertyChangeListener>> getListenerMap() {
		if (listenerMap == null) {
			listenerMap = new HashMap<String, List<PropertyChangeListener>>();
		}
		return listenerMap;
	}

	private Map<Class<?>, Object> getComputeInstanceMap() {
		if (computeInstanceMap == null) {
			computeInstanceMap = new HashMap<Class<?>, Object>();
		}
		return computeInstanceMap;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object res = null;
		if (isStraightGetComponentClassMethod(method)) {
			return componentClass;
		}
		ComponentBeanMethod bm = getDescriptor().getComponentBeanMethod(method.toGenericString());
		res = invoke(bm, proxy, method, args);
		return res;
	}

	private boolean isStraightGetComponentClassMethod(Method method) {
		if (!Class.class.equals(method.getReturnType())) {
			return false;
		}
		if (method.getParameterTypes().length != 0) {
			return false;
		}
		String name = method.getName();
		if (name.equals("straightGetComponentClass")) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	protected Object invoke(ComponentBeanMethod bm, Object proxy, Method method, Object[] args) throws Throwable {
		Object res = null;
		switch (bm) {
			case COMPUTED_GET:
				res = computed(proxy, bm.inferName(method) + bm.name(), args);
				break;
			case COMPUTED_SET:
				res = computed(proxy, bm.inferName(method) + bm.name(), args);
				break;
			case GET:
				res = getter(proxy, bm.inferName(method));
				break;
			case SET:
				setter(proxy, bm.inferName(method), args[0]);
				break;
			case TO_STRING:
				res = buildToString();
				break;
			case EQUALS:
				res = isEquals(proxy, args[0]);
				break;
			case HASHCODE:
				res = getHashCode(proxy);
				break;
			case ADD_PROPERTY_CHANGE_LISTENER:
				addPropertyChangeListener((PropertyChangeListener) args[0]);
				break;
			case ADD_PROPERTY_CHANGE_LISTENER_WITH_PROPERTY_NAME:
				addPropertyChangeListener((String) args[0], (PropertyChangeListener) args[1]);
				break;
			case REMOVE_PROPERTY_CHANGE_LISTENER:
				removePropertyChangeListener((PropertyChangeListener) args[0]);
				break;
			case REMOVE_PROPERTY_CHANGE_LISTENER_WITH_PROPERTY_NAME:
				removePropertyChangeListener((String) args[0], (PropertyChangeListener) args[1]);
				break;
			case STRAIGHT_GET_PROPERTIES:
				res = straightGetProperties(proxy);
				break;
			case STRAIGHT_GET_PROPERTY:
				res = straightGetProperty(proxy, (String) args[0]);
				break;
			case STRAIGHT_GET_PROPERTY_NAMES:
				res = straightGetPropertyNames(proxy);
				break;
			case STRAIGHT_GET_PROPERTY_CLASS:
				res = straightGetPropertyClass(proxy, (String) args[0]);
				break;
			case STRAIGHT_SET_PROPERTIES:
				straightSetProperties(proxy, (Map<String, Object>) args[0]);
				break;
			case STRAIGHT_SET_PROPERTY:
				straightSetProperty(proxy, (String) args[0], args[1]);
				break;
			case CALL:
				if (method.getDeclaringClass() == Object.class) {
					res = method.invoke(this, args);
				}
				break;
			default:
				break;
		}
		if (res == null && method.getReturnType().isPrimitive() && !method.getReturnType().equals(void.class)) {
			throw new IllegalArgumentException("Method " + method + " return a primitive, result is null");
		}
		return res;
	}

	protected Object computed(Object proxy, String propertyName, Object[] args) {
		Object res = null;
		ComputedMethodDescriptor computedMethodDescriptor = getDescriptor().getComputedMethodDescriptor(propertyName);
		if (computedMethodDescriptor != null) {
			Class<?> clazz = computedMethodDescriptor.getComputeClass();
			Object instance = getComputeInstanceMap().get(clazz);
			if (instance == null) {
				instance = ComponentFactory.getInstance().getComputedFactory().createInstance(clazz);
				getComputeInstanceMap().put(clazz, instance);
			}
			try {
				List<Object> list = new ArrayList<Object>();
				list.add(proxy);
				if (args != null && args.length > 0) {
					list.addAll(Arrays.asList(args));
				}
				res = computedMethodDescriptor.getMethod().invoke(instance, list.toArray());
			} catch (IllegalArgumentException e) {
				LOG.error(e, e);
			} catch (IllegalAccessException e) {
				LOG.error(e, e);
			} catch (InvocationTargetException e) {
				LOG.error(e, e);
			}
		}
		return res;
	}

	protected void straightSetProperties(Object proxy, Map<String, Object> properties) {
		if (properties == null) {
			throw new NullArgumentException("properties is null");
		}
		for (Entry<String, Object> entry : properties.entrySet()) {
			setter(proxy, entry.getKey(), entry.getValue());
		}
	}

	protected void straightSetProperty(Object proxy, String propertyName, Object value) {
		try {
			if (getDescriptor().getComputedMethodDescriptor(propertyName + ComponentBeanMethod.COMPUTED_SET.name()) != null) {
				computed(proxy, propertyName + ComponentBeanMethod.COMPUTED_SET.name(), new Object[]{value});
				return;
			}
		} catch (Exception e) {
		}
		setter(proxy, propertyName, value);
	}

	protected Map<String, Object> straightGetProperties(Object proxy) {
		Map<String, Object> res = new HashMap<String, Object>();
		for (Entry<String, Object> entry : propertyValueMap.entrySet()) {
			res.put(entry.getKey(), entry.getValue());
		}
		return res;
	}

	/**
	 * Optimized... almost!
	 *
	 * @param proxy
	 * @param propertyName
	 * @return
	 */
	protected final Object straightGetProperty(Object proxy, String propertyName) {
		try {
			if (getDescriptor().getComputedMethodDescriptor(propertyName + ComponentBeanMethod.COMPUTED_GET.name()) != null) {
				return computed(proxy, propertyName + ComponentBeanMethod.COMPUTED_GET.name(), null);
			}
		} catch (Exception e) {
		}
		return getter(proxy, propertyName);
	}

	protected Set<String> straightGetPropertyNames(Object proxy) {
		return getDescriptor().getPropertyNames();
	}

	protected Class<?> straightGetPropertyClass(Object proxy, String propertyName) {
		if (propertyName == null) {
			throw new NullArgumentException("propertyName is null");
		}
		return getDescriptor().getPropertyClass(propertyName);
	}

	protected Object getter(Object proxy, String propertyName) {
		if (propertyName == null) {
			throw new NullArgumentException("propertyName is null");
		}
		return propertyValueMap.get(propertyName);
	}

	protected void setter(Object proxy, String propertyName, Object value) {
		if (propertyName == null) {
			throw new NullArgumentException("propertyName is null");
		}
		if (!propertyValueMap.containsKey(propertyName)) {
			throw new IllegalArgumentException("propertyName=" + propertyName + " is not property class " + proxy);
		}
		Class<?> propertyClass = componentDescriptor.getPropertyClass(propertyName);
		Class<?> fakeType = PrimitiveHelper.determineClass(propertyClass);
		if (value != null && !fakeType.isAssignableFrom(value.getClass())) {
			throw new IllegalArgumentException("propertyName=" + propertyName + " is not assignable with class=" + value.getClass() + " value=" + value + " " + proxy);
		}
		if (value == null && propertyClass.isPrimitive()) {
			throw new IllegalArgumentException("propertyName=" + propertyName + " is primitive and value is null " + proxy);
		}
		Object oldValue = propertyValueMap.get(propertyName);
		propertyValueMap.put(propertyName, value);
		firePropertyChange(proxy, propertyName, oldValue, value);
	}

	protected void addPropertyChangeListener(PropertyChangeListener l) {
		if (l == null) {
			throw new NullArgumentException("l is null");
		}
		getListeners().add(l);
	}

	protected void addPropertyChangeListener(String propertyName, PropertyChangeListener l) {
		if (propertyName == null) {
			throw new NullArgumentException("propertyName is null");
		}
		if (l == null) {
			throw new NullArgumentException("l is null");
		}
		List<PropertyChangeListener> list = getListenerMap().get(propertyName);
		if (list == null) {
			list = new ArrayList<PropertyChangeListener>();
			getListenerMap().put(propertyName, list);
		}
		list.add(l);
	}

	protected void removePropertyChangeListener(PropertyChangeListener l) {
		if (l == null) {
			throw new NullArgumentException("l is null");
		}
		getListeners().remove(l);
	}

	protected void removePropertyChangeListener(String propertyName, PropertyChangeListener l) {
		if (propertyName == null) {
			throw new NullArgumentException("propertyName is null");
		}
		if (l == null) {
			throw new NullArgumentException("l is null");
		}
		List<PropertyChangeListener> list = getListenerMap().get(propertyName);
		if (list != null) {
			list.remove(l);
		}
	}

	protected void firePropertyChange(Object proxy, String propertyName, Object oldValue, Object newValue) {
		PropertyChangeEvent evt = new PropertyChangeEvent(proxy, propertyName, oldValue, newValue);
		for (PropertyChangeListener l : getListeners()) {
			l.propertyChange(evt);
		}
		List<PropertyChangeListener> list = getListenerMap().get(propertyName);
		if (list != null) {
			for (PropertyChangeListener l : list) {
				l.propertyChange(evt);
			}
		}
	}

	protected int getHashCode(Object proxy) {
		ComponentDescriptor cd = getDescriptor();
		if (cd.getEqualsKeyPropertyNames() != null && !cd.getEqualsKeyPropertyNames().isEmpty()) {
			int hashCode = 23;
			for (String propertyName : cd.getEqualsKeyPropertyNames()) {
				Class<?> type = cd.getPropertyClass(propertyName);
				Object value = propertyValueMap.get(propertyName);
				if (type.isArray()) {
					hashCode = (hashCode * 37) + PrimitiveHelper.arrayHashCode(type, value);
				} else {
					hashCode = (hashCode * 37) + (value == null ? 1 : value.hashCode());
				}
			}
			return hashCode;
		} else {
			return hashCode();
		}
	}

	protected boolean isEquals(Object proxy, Object o) {
		ComponentDescriptor cd = getDescriptor();
		if (cd.getEqualsKeyPropertyNames() != null && !cd.getEqualsKeyPropertyNames().isEmpty()) {
			if (proxy == o) {
				return true;
			}
			if (o == null) {
				return false;
			}

			if (!(o instanceof IComponent)) {
				return false;
			}
			Class<? extends IComponent> clazz = ComponentFactory.getInstance().getComponentClass((IComponent) o);
			if (!componentClass.equals(clazz)) {
				return false;
			}
			IComponent other = (IComponent) o;
			for (String propertyName : cd.getEqualsKeyPropertyNames()) {
				Object value1 = propertyValueMap.get(propertyName);
				Object value2 = other.straightGetProperty(propertyName);
				if (!Objects.deepEquals(value1, value2)) {
					return false;
				}
			}
			return true;
		} else {
			return proxy == o;
		}
	}

	protected String buildToString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getDescriptor().getName()).append("@").append(this.hashCode()).append(" {").append("\n");
		boolean first = true;
		for (Entry<String, Object> entry : propertyValueMap.entrySet()) {
			if (!first) {
				sb.append(",\n");
			} else {
				first = false;
			}
			String text = null;
			if (entry.getValue() != null) {
				Object value = entry.getValue();
				if (value.getClass().isArray()) {
					Object[] objectArray = {value};
					String arrayString = Arrays.deepToString(objectArray);
					text = arrayString.substring(1, arrayString.length() - 1);
				} else {
					if (entry.getValue() instanceof String) {
						text = "\"" + entry.getValue().toString() + "\"";
					} else {
						text = value.toString();
					}
				}
				if (text != null) {
					text = text.replaceAll("\n", "\n\t");
				}
			}

			sb.append("\t\"").append(entry.getKey()).append("\" : ").append(text);
		}
		sb.append("\n}");
		return sb.toString();
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		// out.writeObject(componentClass);
		out.writeObject(componentClassName);
		out.writeObject(propertyValueMap);
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		// componentClass = (Class<? extends IComponent>) in.readObject();
		Object firstObject = in.readObject();
		if (firstObject instanceof String) {
			componentClassName = (String) firstObject;
			componentClass = (Class<? extends IComponent>) Class.forName(componentClassName);
		} else { // retrocompatibility
			componentClass = (Class<? extends IComponent>) firstObject;
			componentClassName = componentClass.getName();
		}
		propertyValueMap = (Map<String, Object>) in.readObject();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
