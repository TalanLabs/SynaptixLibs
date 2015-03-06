package com.synaptix.mybatis.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectionException;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.BaseWrapper;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;

public class ComponentObjectWrapper extends BaseWrapper {

	private final IComponent component;

	private final ComponentDescriptor componentDescriptor;

	private final String[] propertyNames;

	private final Map<String, String> ignoreCasePropertyNameMap;

	public ComponentObjectWrapper(MetaObject metaObject, IComponent component) {
		super(metaObject);
		this.component = component;
		this.componentDescriptor = ComponentFactory.getInstance().getDescriptor(component);
		this.propertyNames = componentDescriptor.getPropertyNames().toArray(new String[componentDescriptor.getPropertyNames().size()]);

		ignoreCasePropertyNameMap = new HashMap<String, String>();
		for (String propertyName : propertyNames) {
			ignoreCasePropertyNameMap.put(propertyName.toUpperCase(Locale.ENGLISH), propertyName);
		}
	}

	public Object get(PropertyTokenizer prop) {
		if (prop.getIndex() != null) {
			Object collection = resolveCollection(prop, component);
			return getCollectionValue(prop, collection);
		} else {
			return getBeanProperty(prop);
		}
	}

	public void set(PropertyTokenizer prop, Object value) {
		if (prop.getIndex() != null) {
			Object collection = resolveCollection(prop, component);
			setCollectionValue(prop, collection, value);
		} else {
			setBeanProperty(prop, value);
		}
	}

	public String findProperty(String name, boolean useCamelCaseMapping) {
		if (useCamelCaseMapping) {
			name = name.replace("_", "");
		}
		StringBuilder prop = buildProperty(name, new StringBuilder());
		return prop.length() > 0 ? prop.toString() : null;
	}

	private StringBuilder buildProperty(String name, StringBuilder builder) {
		// PropertyTokenizer prop = new PropertyTokenizer(name);
		// if (prop.hasNext()) {
		// String propertyName = ignoreCasePropertyNameMap.get(prop.getName().toUpperCase(Locale.ENGLISH));
		// if (propertyName != null) {
		// builder.append(propertyName);
		// builder.append(".");
		//
		// componentDescriptor.getPropertyClass(name);
		// metaValue = MetaObject.forObject(newObject, metaObject.getObjectFactory(), metaObject.getObjectWrapperFactory());
		// MetaClass metaProp = metaClassForProperty(propertyName);
		// metaProp.buildProperty(prop.getChildren(), builder);
		// }
		// } else {
		// String propertyName = ignoreCasePropertyNameMap.get(name.toUpperCase(Locale.ENGLISH));
		// if (propertyName != null) {
		// builder.append(propertyName);
		// }
		// }
		return builder;
	}

	public String[] getGetterNames() {
		return propertyNames;
	}

	public String[] getSetterNames() {
		return propertyNames;
	}

	public Class<?> getSetterType(String name) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
			if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
				return componentDescriptor.getPropertyClass(name);
			} else {
				return metaValue.getSetterType(prop.getChildren());
			}
		} else {
			return componentDescriptor.getPropertyClass(name);
		}
	}

	public Class<?> getGetterType(String name) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
			if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
				return componentDescriptor.getPropertyClass(name);
			} else {
				return metaValue.getGetterType(prop.getChildren());
			}
		} else {
			return componentDescriptor.getPropertyClass(name);
		}
	}

	public boolean hasSetter(String name) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
			if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
				return componentDescriptor.getPropertyDescriptor(name) != null;
			} else {
				return metaValue.hasSetter(prop.getChildren());
			}
		} else {
			return componentDescriptor.getPropertyDescriptor(name) != null;
		}
	}

	public boolean hasGetter(String name) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedName());
			if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
				return componentDescriptor.getPropertyDescriptor(name) != null;
			} else {
				return metaValue.hasGetter(prop.getChildren());
			}
		} else {
			return componentDescriptor.getPropertyDescriptor(name) != null;
		}
	}

	public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
		MetaObject metaValue;
		Class<?> type = getSetterType(prop.getName());
		try {
			Object newObject = objectFactory.create(type);
			metaValue = MetaObject.forObject(newObject, metaObject.getObjectFactory(), metaObject.getObjectWrapperFactory());
			set(prop, newObject);
		} catch (Exception e) {
			throw new ReflectionException("Cannot set value of property '" + name + "' because '" + name + "' is null and cannot be instantiated on instance of " + type.getName() + ". Cause:"
					+ e.toString(), e);
		}
		return metaValue;
	}

	private Object getBeanProperty(PropertyTokenizer prop) {
		try {
			try {
				return component.straightGetProperty(prop.getName());
			} catch (Throwable t) {
				throw ExceptionUtil.unwrapThrowable(t);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable t) {
			throw new ReflectionException("Could not get property '" + prop.getName() + "' from " + componentDescriptor.getComponentClass() + ".  Cause: " + t.toString(), t);
		}
	}

	private void setBeanProperty(PropertyTokenizer prop, Object value) {
		try {
			try {
				component.straightSetProperty(prop.getName(), value);
			} catch (Throwable t) {
				throw ExceptionUtil.unwrapThrowable(t);
			}
		} catch (Throwable t) {
			throw new ReflectionException("Could not set property '" + prop.getName() + "' of '" + componentDescriptor.getComponentClass() + "' with value '" + value + "' Cause: " + t.toString(), t);
		}
	}

	public boolean isCollection() {
		return false;
	}

	public void add(Object element) {
		throw new UnsupportedOperationException();
	}

	public <E> void addAll(List<E> list) {
		throw new UnsupportedOperationException();
	}
}
