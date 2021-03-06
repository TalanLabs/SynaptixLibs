package com.synaptix.component.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentDescriptor.PropertyDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.field.IField;

public final class ComponentHelper {

	/**
	 * Set a null values for component
	 */
	public static <E extends IComponent> void setNullValues(E sourceEntity, String[] fieldNames) {
		if (sourceEntity != null && fieldNames != null) {
			for (String propertyName : fieldNames) {
				if (sourceEntity.straightGetPropertyNames().contains(propertyName)) {
					if (!sourceEntity.straightGetPropertyClass(propertyName).isPrimitive()) {
						sourceEntity.straightSetProperty(propertyName, null);
					}
				}
			}
		}
	}

	/**
	 * Build a components map with list and propertyName
	 */
	@SuppressWarnings("unchecked")
	public static <F, E extends IComponent> Map<F, List<E>> buildComponentsMap(Collection<E> components, String keyPropertyName) {
		Map<F, List<E>> map = new HashMap<F, List<E>>();
		if (components != null && !components.isEmpty()) {
			for (E component : components) {
				F key = (F) component.straightGetProperty(keyPropertyName);
				if (key != null) {
					List<E> list = map.get(key);
					if (list == null) {
						list = new ArrayList<E>();
						map.put(key, list);
					}
					list.add(component);
				}
			}
		}
		return map;
	}

	/**
	 * Extract property name in components list
	 */
	@SuppressWarnings("unchecked")
	public static <F, E extends IComponent> List<F> extractValues(Collection<E> components, String propertyName) {
		List<F> res = new ArrayList<F>();
		if (components != null && !components.isEmpty()) {
			for (E component : components) {
				F value = component != null ? (F) component.straightGetProperty(propertyName) : null;
				res.add(value);
			}
		}
		return res;
	}

	/**
	 * Find a component by propertyName and value
	 */
	public static <E extends IComponent> E findComponentBy(Collection<E> components, String propertyName, Object value) {
		E res = null;
		if (components != null && !components.isEmpty()) {
			Iterator<E> it = components.iterator();
			while (it.hasNext() && res == null) {
				E component = it.next();
				Object val = component.straightGetProperty(propertyName);
				if (ObjectUtils.equals(val, value)) {
					res = component;
				}
			}
		}
		return res;
	}

	/**
	 * Get index for component list
	 */
	public static <E extends IComponent> int indexComponentOf(Collection<E> components, String propertyName, Object value) {
		int res = -1;
		if (value != null && components != null && !components.isEmpty()) {
			int i = 0;
			Iterator<E> it = components.iterator();
			while (it.hasNext() && res == -1) {
				E component = it.next();
				Object val = component.straightGetProperty(propertyName);
				if (ObjectUtils.equals(val, value)) {
					res = i;
				}
				i++;
			}
		}
		return res;
	}

	/**
	 * Find all components by propertyName and value
	 */
	public static <E extends IComponent> List<E> findComponentsBy(Collection<E> components, String propertyName, Object value) {
		List<E> res = new ArrayList<E>();
		if (components != null && !components.isEmpty()) {
			for (E component : components) {
				Object val = component.straightGetProperty(propertyName);
				if (ObjectUtils.equals(val, value)) {
					res.add(component);
				}
			}
		}
		return res;
	}

	/**
	 * Get value for component, toto.tata
	 */
	@SuppressWarnings("unchecked")
	public static <F> F getValue(IComponent component, String propertyName) {
		if (component == null) {
			return null;
		}
		int index = propertyName.indexOf('.');
		if (index == -1) {
			return (F) component.straightGetProperty(propertyName);
		} else {
			String head = propertyName.substring(0, index);
			F temp = (F) component.straightGetProperty(head);
			if (!(temp instanceof IComponent)) {
				return temp;
			}
			return getValue((IComponent) temp, propertyName.substring(index + 1));
		}
	}

	/**
	 * Get component class for property, toto.tata
	 */
	@SuppressWarnings("unchecked")
	public static Class<? extends IComponent> getComponentClass(Class<? extends IComponent> componentClass, String propertyName) {
		if (componentClass == null) {
			return null;
		}
		int index = propertyName.indexOf('.');
		if (index == -1) {
			return componentClass;
		} else {
			String head = propertyName.substring(0, index);
			ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(componentClass);
			PropertyDescriptor pd = cd.getPropertyDescriptor(head);
			if (pd == null || !IComponent.class.isAssignableFrom(pd.getPropertyClass())) {
				return null;
			}
			return getComponentClass((Class<? extends IComponent>) pd.getPropertyClass(), propertyName.substring(index + 1));
		}
	}

	/**
	 * Get final class for property, toto.tata.date
	 */
	@SuppressWarnings("unchecked")
	public static Class<?> getFinalClass(Class<? extends IComponent> componentClass, String propertyName) {
		if (componentClass == null) {
			return null;
		}
		int index = propertyName.indexOf('.');
		ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(componentClass);
		if (index == -1) {
			return cd.getPropertyClass(propertyName);
		} else {
			String head = propertyName.substring(0, index);
			PropertyDescriptor pd = cd.getPropertyDescriptor(head);
			if (pd == null || !IComponent.class.isAssignableFrom(pd.getPropertyClass())) {
				return null;
			}
			return getFinalClass((Class<? extends IComponent>) pd.getPropertyClass(), propertyName.substring(index + 1));
		}
	}

	/**
	 * Get component descriptor for property, toto.tata.date
	 */
	@SuppressWarnings("unchecked")
	public static PropertyDescriptor getFinalPropertyDescriptor(Class<? extends IComponent> componentClass, String propertyName) {
		if (componentClass == null) {
			return null;
		}
		int index = propertyName.indexOf('.');
		ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(componentClass);
		if (index == -1) {
			return cd.getPropertyDescriptor(propertyName);
		} else {
			String head = propertyName.substring(0, index);
			PropertyDescriptor pd = cd.getPropertyDescriptor(head);
			if (pd == null || !IComponent.class.isAssignableFrom(pd.getPropertyClass())) {
				return null;
			}
			return getFinalPropertyDescriptor((Class<? extends IComponent>) pd.getPropertyClass(), propertyName.substring(index + 1));
		}
	}

	/**
	 * Get a property name for property, toto.tata
	 */
	@SuppressWarnings("unchecked")
	public static String getPropertyName(Class<? extends IComponent> componentClass, String propertyName) {
		if (propertyName == null) {
			return null;
		}
		int index = propertyName.indexOf('.');
		if (index == -1) {
			return propertyName;
		} else {
			String head = propertyName.substring(0, index);
			ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(componentClass);
			PropertyDescriptor pd = cd.getPropertyDescriptor(head);
			if (pd == null || !IComponent.class.isAssignableFrom(pd.getPropertyClass())) {
				return null;
			}
			return getPropertyName((Class<? extends IComponent>) pd.getPropertyClass(), propertyName.substring(index + 1));
		}
	}

	/**
	 * Clone component and all childrens
	 */
	@SuppressWarnings("unchecked")
	public static <E extends IComponent> E clone(E component) {
		E res = null;
		if (component != null) {
			byte[] bs = null;

			ByteArrayOutputStream baos = null;
			ObjectOutputStream oos = null;
			try {
				baos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(baos);
				oos.writeObject(component);

				bs = baos.toByteArray();
			} catch (Exception e) {
			} finally {
				if (oos != null) {
					try {
						oos.close();
					} catch (Exception e) {
					}
				}
				if (baos != null) {
					try {
						baos.close();
					} catch (Exception e) {
					}
				}
			}
			if (bs != null) {
				ByteArrayInputStream bais = null;
				ObjectInputStream ois = null;
				try {
					bais = new ByteArrayInputStream(bs);
					ois = new ObjectInputStream(bais);
					res = (E) ois.readObject();
				} catch (Exception e) {
				} finally {
					if (ois != null) {
						try {
							ois.close();
						} catch (Exception e) {
						}
					}
					if (bais != null) {
						try {
							bais.close();
						} catch (Exception e) {
						}
					}
				}
			}
		}
		return res;
	}

	/**
	 * Convert a list of component extended to list of component
	 */
	public static <E extends IComponent, G extends E> List<E> convertComponentExtendedToComponent(Class<E> componentClass, Collection<G> componentExtendeds) {
		if (componentExtendeds == null) {
			return null;
		}
		List<E> res = new ArrayList<E>();
		if (!componentExtendeds.isEmpty()) {
			for (G componentExtended : componentExtendeds) {
				res.add(componentExtended);
			}
		}
		return res;
	}

	/**
	 * Serialize a component into a map (recursively)
	 */
	public static <E extends IComponent> Map<String, Serializable> serializeComponent(E component) {
		Map<String, Serializable> map = null;
		if (component != null) {
			map = new HashMap<String, Serializable>();
			ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(component);
			for (String property : cd.getPropertyNames()) {
				Object value = component.straightGetProperty(property);
				if (value != null) {
					if (Serializable.class.isAssignableFrom(cd.getPropertyClass(property))) {
						Serializable serializable = (Serializable) value;
						if (IComponent.class.isAssignableFrom(cd.getPropertyClass(property))) {
							serializable = (Serializable) serializeComponent((IComponent) serializable);
						}
						map.put(property, serializable);
					}
				}
			}
		}
		return map;
	}

	/**
	 * Deserialize a map into a component of given class
	 */
	@SuppressWarnings("unchecked")
	public static <E extends IComponent> E deserializeComponent(Class<E> componentClass, Map<String, Serializable> map) {
		E component = null;
		ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(componentClass);
		if (map != null) {
			component = ComponentFactory.getInstance().createInstance(componentClass);
			for (Entry<String, Serializable> entry : map.entrySet()) {
				Class<?> propertyClass = cd.getPropertyClass(entry.getKey());
				if (propertyClass != null) {
					Object value = entry.getValue();
					if ((IComponent.class.isAssignableFrom(propertyClass)) && (Map.class.isAssignableFrom(value.getClass()))) {
						value = deserializeComponent((Class<IComponent>) propertyClass, (Map<String, Serializable>) value);
					}
					component.straightSetProperty(entry.getKey(), value);
				}
			}
		}
		return component;
	}

	/**
	 * Copy properties from srcComponent to dstComponent
	 * @param propertyNames
	 *            toto.tata, id, version
	 */
	public static <E extends IComponent> void copy(E srcComponent, E dstComponent, Set<String> propertyNames) {
		Class<E> componentClass = ComponentFactory.getInstance().getComponentClass(srcComponent);

		ComponentDescriptor componentDescriptor = ComponentFactory.getInstance().getDescriptor(componentClass);
		if (propertyNames != null) {
			for (String propertyName : propertyNames) {
				String head;
				String tail;
				int index = propertyName.indexOf('.');
				if (index == -1) {
					head = propertyName;
					tail = null;
				} else {
					head = propertyName.substring(0, index);
					tail = propertyName.substring(index + 1);
				}

				if (componentDescriptor.getPropertyNames().contains(head)) {
					Object value = srcComponent.straightGetProperty(head);

					if (tail != null && value instanceof IComponent) {
						Object dstValue = dstComponent.straightGetProperty(head);
						if (dstValue == null) {
							dstValue = ComponentFactory.getInstance().createInstance(ComponentFactory.getInstance().getComponentClass((IComponent) value));
							dstComponent.straightSetProperty(head, dstValue);
						}
						Set<String> ss = new HashSet<String>();
						ss.add(tail);
						copy((IComponent) value, (IComponent) dstValue, ss);
					} else {
						dstComponent.straightSetProperty(head, value);
					}
				}
			}
		}
	}

	/**
	 * Build a dot property name
	 * 
	 * toto.name
	 * 
	 * tata.id
	 * 
	 * toto.tata.id
	 * 
	 * @author Gaby
	 * 
	 */
	public static final class PropertyDotBuilder {

		private List<String> propertyNames;

		public PropertyDotBuilder() {
			super();
			propertyNames = new ArrayList<String>();
		}

		/**
		 * Add array enum
		 */
		public PropertyDotBuilder addPropertyNames(IField... propertyNames) {
			for (IField propertyName : propertyNames) {
				addPropertyName(propertyName);
			}
			return this;
		}

		/**
		 * Add a enum
		 */
		private PropertyDotBuilder addPropertyName(IField propertyName) {
			assert propertyName != null;
			propertyNames.add(propertyName.name());
			return this;
		}

		/**
		 * Add a array string
		 */
		public PropertyDotBuilder addPropertyNames(String... propertyNames) {
			for (String propertyName : propertyNames) {
				addPropertyName(propertyName);
			}
			return this;
		}

		/**
		 * Add a string
		 */
		private PropertyDotBuilder addPropertyName(String propertyName) {
			assert propertyName != null;
			propertyNames.add(propertyName);
			return this;
		}

		/**
		 * build
		 */
		public String build() {
			return build(propertyNames.toArray(new String[propertyNames.size()]));
		}

		/**
		 * Build a dot property name with string
		 * 
		 * @param propertyNames
		 *            toto, tata, id
		 * @return toto.tata.id
		 */
		public static String build(String... propertyNames) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (String propertyName : propertyNames) {
				if (first) {
					first = false;
				} else {
					sb.append(".");
				}
				sb.append(propertyName);
			}
			return sb.toString();
		}

		/**
		 * Build a dot property name with enum
		 * 
		 * @param propertyNames
		 *            Enum.toto, Enum.tata, Enum.id
		 * @return toto.tata.id
		 */
		public static String build(IField... propertyNames) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for (IField propertyName : propertyNames) {
				if (first) {
					first = false;
				} else {
					sb.append(".");
				}
				sb.append(propertyName.name());
			}
			return sb.toString();
		}
	}

	/**
	 * Build array of property name
	 * 
	 * @author Gaby
	 * 
	 */
	public static final class PropertyArrayBuilder {

		private List<String> propertyNames;

		public PropertyArrayBuilder() {
			super();
			propertyNames = new ArrayList<String>();
		}

		/**
		 * Add array enum
		 */
		public PropertyArrayBuilder addPropertyNames(IField... propertyNames) {
			for (IField propertyName : propertyNames) {
				addPropertyName(propertyName);
			}
			return this;
		}

		/**
		 * Add a dot property name
		 * 
		 * @param propertyNames
		 *            Enum.toto, Enum.tata, Enum.id -> toto.tata.id
		 */
		public PropertyArrayBuilder addDotPropertyName(IField... propertyNames) {
			addPropertyName(PropertyDotBuilder.build(propertyNames));
			return this;
		}

		/**
		 * Add a property name
		 */
		private PropertyArrayBuilder addPropertyName(IField propertyName) {
			assert propertyName != null;
			propertyNames.add(propertyName.name());
			return this;
		}

		/**
		 * Add a property name
		 */
		public PropertyArrayBuilder addPropertyNames(String... propertyNames) {
			for (String propertyName : propertyNames) {
				addPropertyName(propertyName);
			}
			return this;
		}

		/**
		 * Add a dot property name
		 * 
		 * @param propertyNames
		 *            toto, tata, id -> toto.tata.id
		 */
		public PropertyArrayBuilder addDotPropertyName(String... propertyNames) {
			addPropertyName(PropertyDotBuilder.build(propertyNames));
			return this;
		}

		/**
		 * Add a property name
		 */
		private PropertyArrayBuilder addPropertyName(String propertyName) {
			assert propertyName != null;
			propertyNames.add(propertyName);
			return this;
		}

		public String[] build() {
			return propertyNames.toArray(new String[propertyNames.size()]);
		}

		/**
		 * Build a array with enum
		 */
		public static String[] build(IField... es) {
			String[] res = new String[es.length];
			int i = 0;
			for (IField e : es) {
				res[i] = e.name();
				i++;
			}
			return res;
		}

		/**
		 * Build a array
		 */
		public static String[] build(String... ss) {
			return ss;
		}
	}
}
