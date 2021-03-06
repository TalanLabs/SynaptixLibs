package com.synaptix.component.factory;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.synaptix.component.IComponent;
import com.synaptix.component.extension.IClassExtensionDescriptor;
import com.synaptix.component.extension.IComponentExtensionProcessor;
import com.synaptix.component.extension.IPropertyExtensionDescriptor;

/**
 * ComponentDescriptor, describe a component class <link>IComponent</link>
 *
 * @author Gaby
 */
public class ComponentDescriptor implements Serializable {

	private static final long serialVersionUID = -4548645309704022750L;

	protected Map<String, PropertyDescriptor> componentFieldMap;

	protected Set<PropertyDescriptor> propertyDescriptors;

	protected Class<? extends IComponent> componentClass;

	protected Set<String> propertyNames;

	protected Map<String, ComponentBeanMethod> methodMap;

	protected Map<String, ComputedMethodDescriptor> computedMethodDescriptorMap;

	protected Set<String> equalsKeyPropertyNames;

	protected Map<String, Boolean> nullEqualsKeyMap;

	protected Map<Class<?>, IClassExtensionDescriptor> classExtensionDescriptorMap;

	ComponentDescriptor(Class<? extends IComponent> componentClass) {
		super();

		if (componentClass == null) {
			throw new NullArgumentException("componentClass is null");
		}

		this.componentClass = componentClass;

		initialize();
	}

	protected void initialize() {
		componentFieldMap = new HashMap<String, PropertyDescriptor>();
		propertyDescriptors = new HashSet<PropertyDescriptor>();
		equalsKeyPropertyNames = new HashSet<String>();
		propertyNames = new HashSet<String>();
		methodMap = new HashMap<String, ComponentBeanMethod>();
		classExtensionDescriptorMap = new HashMap<Class<?>, IClassExtensionDescriptor>();
		computedMethodDescriptorMap = new HashMap<String, ComputedMethodDescriptor>();
		nullEqualsKeyMap = new HashMap<String, Boolean>();

		Set<Class<?>> extensionClasss = ComponentFactory.getInstance().getExtensionClasss();
		if (extensionClasss != null && !extensionClasss.isEmpty()) {
			for (Class<?> extensionClass : extensionClasss) {
				IComponentExtensionProcessor componentExtensionProcessor = ComponentFactory.getInstance().getExtension(extensionClass);
				IClassExtensionDescriptor classExtensionDescriptor = componentExtensionProcessor.createClassExtensionDescriptor(componentClass);
				if (classExtensionDescriptor != null) {
					classExtensionDescriptorMap.put(extensionClass, classExtensionDescriptor);
				}
			}
		}

		addMethods(componentClass);
		addMethods(Object.class);

		propertyDescriptors = Collections.unmodifiableSet(propertyDescriptors);
		equalsKeyPropertyNames = Collections.unmodifiableSet(equalsKeyPropertyNames);
		propertyNames = Collections.unmodifiableSet(propertyNames);
	}

	protected void addMethods(Class<?> clazz) {
		for (Method method : clazz.getMethods()) {
			ComponentBeanMethod bm = ComponentBeanMethod.which(method);
			methodMap.put(method.toGenericString(), bm);
			addMethod(method, bm);
		}
	}

	protected void addMethod(Method method, ComponentBeanMethod bm) {
		if ((ComponentBeanMethod.COMPUTED_GET == bm) || (ComponentBeanMethod.COMPUTED_SET == bm)) {
			IComponent.Computed computed = method.getAnnotation(IComponent.Computed.class);
			Class<?> clazz = computed.value();
			Method m = findMethod(clazz, method);
			if (m != null) {
				String propertyName = bm.inferName(method);
				ComputedMethodDescriptor computedMethodDescriptor = new ComputedMethodDescriptor(method.toGenericString(), clazz, m);
				computedMethodDescriptorMap.put(propertyName + bm.name(), computedMethodDescriptor);
				addPropertyName(method, bm, propertyName);
			} else {
				List<Class<?>> ps = new ArrayList<Class<?>>();
				ps.add(componentClass);
				ps.addAll(Arrays.asList(method.getParameterTypes()));

				throw new RuntimeException(method.getReturnType() + " " + method.getName() + "(" + Arrays.toString(ps.toArray()) + ") not found in " + clazz);
			}
		} else if (ComponentBeanMethod.GET == bm) {
			String propertyName = bm.inferName(method);
			addPropertyName(method, bm, propertyName);
		}
	}

	private Method findMethod(Class<?> clazz, Method method) {
		if (clazz != null) {
			List<Class<?>> ps = new ArrayList<Class<?>>();
			ps.add(componentClass);
			ps.addAll(Arrays.asList(method.getParameterTypes()));
			try {
				Method[] methods = clazz.getMethods();
				if (methods != null) {
					for (Method m : methods) {
						if ((m.getName().equals(method.getName())) && (m.getParameterTypes().length == ps.size())) {
							Class<?>[] parameterTypes = m.getParameterTypes();
							boolean equal = true;
							for (int i = 0; i < parameterTypes.length && equal; i++) {
								if (!parameterTypes[i].isAssignableFrom(ps.get(i))) {
									equal = false;
								}
							}
							if ((equal) && (m.getReturnType().isAssignableFrom(method.getReturnType()))) {
								return m;
							}
						}
					}
				}
			} catch (SecurityException e) {
			}
		}
		return null;
	}

	protected void addPropertyName(Method method, ComponentBeanMethod bm, String propertyName) {
		IComponent.EqualsKey a = method.getAnnotation(IComponent.EqualsKey.class);
		boolean equalsKey = a != null;

		Map<Class<?>, IPropertyExtensionDescriptor> propertyExtensionDescriptorMap = new HashMap<Class<?>, IPropertyExtensionDescriptor>();
		Set<Class<?>> extensionClasss = ComponentFactory.getInstance().getExtensionClasss();
		if (extensionClasss != null && !extensionClasss.isEmpty()) {
			for (Class<?> extensionClass : extensionClasss) {
				IComponentExtensionProcessor componentExtensionProcessor = ComponentFactory.getInstance().getExtension(extensionClass);
				IPropertyExtensionDescriptor propertyExtensionDescriptor = componentExtensionProcessor.createPropertyExtensionDescriptor(componentClass, method);
				if (propertyExtensionDescriptor != null) {
					propertyExtensionDescriptorMap.put(extensionClass, propertyExtensionDescriptor);
				}
			}
		}

		Class<?> returnType = null;
		Type type = method.getGenericReturnType();
		if (type instanceof TypeVariable<?>) {
			TypeVariable<?> typeVariable = (TypeVariable<?>) type;
			returnType = TypeHelper.findClass(componentClass, method.getDeclaringClass(), typeVariable.getName());
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			returnType = (Class<?>) parameterizedType.getRawType();
		} else if (type instanceof Class<?>) {
			returnType = (Class<?>) type;
		}
		if (returnType == null) {
			returnType = method.getReturnType();
		}

		PropertyDescriptor cf = new PropertyDescriptor(propertyName, returnType, equalsKey, propertyExtensionDescriptorMap);
		componentFieldMap.put(propertyName, cf);
		propertyDescriptors.add(cf);
		if ((ComponentBeanMethod.COMPUTED_GET != bm) && (ComponentBeanMethod.COMPUTED_SET != bm)) {
			propertyNames.add(propertyName);
		}
		if (equalsKey) {
			equalsKeyPropertyNames.add(propertyName);
			nullEqualsKeyMap.put(propertyName, a.nullEquals());
		}
	}

	/**
	 * @param propertyName equals key property name
	 * @return true if nullEquals
	 */
	public final boolean isNullEqualsKeyPropertyName(String propertyName) {
		Boolean b = nullEqualsKeyMap.get(propertyName);
		return b != null && b;
	}

	/**
	 * Get a simple name from component class
	 *
	 * @return
	 */
	public final String getName() {
		return componentClass.getName();
	}

	/**
	 * Get the component class
	 *
	 * @return
	 */
	public final Class<? extends IComponent> getComponentClass() {
		return componentClass;
	}

	/**
	 * Test if o is instance of component class
	 *
	 * @param o
	 * @return
	 */
	public final boolean isInstance(Object o) {
		return o != null && componentClass.isInstance(o);
	}

	/**
	 * Get a list of properties names
	 *
	 * @return
	 */
	public final Set<String> getPropertyNames() {
		return propertyNames;
	}

	/**
	 * Get a <code>ComponentBeanMethod</code>
	 *
	 * @param keyMethod
	 * @return
	 */
	public final ComponentBeanMethod getComponentBeanMethod(String keyMethod) {
		if (keyMethod == null) {
			throw new NullArgumentException("keyMethod is null");
		}
		return methodMap.get(keyMethod);
	}

	/**
	 * Get a compute method descriptor
	 *
	 * @param key (can be propertyName if getter, or GenericString)
	 * @return
	 */
	public final ComputedMethodDescriptor getComputedMethodDescriptor(String key) {
		if (key == null) {
			throw new NullArgumentException("keyMethod is null");
		}
		return computedMethodDescriptorMap.get(key);
	}

	/**
	 * Get a type of property name
	 *
	 * @param propertyName
	 * @return a type of property
	 */
	public final Class<?> getPropertyClass(String propertyName) {
		if (propertyName == null) {
			throw new NullArgumentException("propertyName is null");
		}
		return componentFieldMap.containsKey(propertyName) ? componentFieldMap.get(propertyName).getPropertyClass() : null;
	}

	/**
	 * Get class extension descriptor
	 *
	 * @param componentExtensionClass
	 * @return
	 */
	public final IClassExtensionDescriptor getClassExtensionDescriptor(Class<?> componentExtensionClass) {
		if (componentExtensionClass == null) {
			throw new NullArgumentException("componentExtensionClass is null");
		}
		return classExtensionDescriptorMap.get(componentExtensionClass);
	}

	/**
	 * Get a component field by property name
	 *
	 * @param propertyName
	 * @return
	 */
	public final PropertyDescriptor getPropertyDescriptor(String propertyName) {
		return componentFieldMap.get(propertyName);
	}

	/**
	 * Get property descriptors
	 *
	 * @return
	 */
	public final Set<PropertyDescriptor> getPropertyDescriptors() {
		return propertyDescriptors;
	}

	/**
	 * Get properties use for equals and hashcode
	 *
	 * @return
	 */
	public final Set<String> getEqualsKeyPropertyNames() {
		return equalsKeyPropertyNames;
	}

	public final static class ComputedMethodDescriptor {

		private final String keyMethod;

		private final Class<?> computeClass;

		private final Method method;

		public ComputedMethodDescriptor(String keyMethod, Class<?> computeClass, Method method) {
			super();
			this.keyMethod = keyMethod;
			this.computeClass = computeClass;
			this.method = method;
		}

		public String getKeyMethod() {
			return keyMethod;
		}

		public Class<?> getComputeClass() {
			return computeClass;
		}

		public Method getMethod() {
			return method;
		}
	}

	public final static class PropertyDescriptor {

		private final String propertyName;

		private final Class<?> propertyClass;

		private final boolean equalsKey;

		private final Map<Class<?>, IPropertyExtensionDescriptor> propertyExtensionDescriptorMap;

		PropertyDescriptor(String propertyName, Class<?> propertyClass, boolean equalsKey, Map<Class<?>, IPropertyExtensionDescriptor> propertyExtensionDescriptorMap) {
			super();

			this.propertyName = propertyName;
			this.propertyClass = propertyClass;
			this.equalsKey = equalsKey;
			this.propertyExtensionDescriptorMap = propertyExtensionDescriptorMap;
		}

		public String getPropertyName() {
			return propertyName;
		}

		public boolean isEqualsKey() {
			return equalsKey;
		}

		public Class<?> getPropertyClass() {
			return propertyClass;
		}

		public IPropertyExtensionDescriptor getPropertyExtensionDescriptor(Class<?> componentExtensionClass) {
			if (componentExtensionClass == null) {
				throw new NullArgumentException("componentExtensionClass is null");
			}
			return propertyExtensionDescriptorMap.get(componentExtensionClass);
		}

		@Override
		public int hashCode() {
			return propertyName.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof PropertyDescriptor) {
				PropertyDescriptor o = (PropertyDescriptor) obj;
				return o.propertyName.equals(propertyName);
			}
			return super.equals(obj);
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}
}
