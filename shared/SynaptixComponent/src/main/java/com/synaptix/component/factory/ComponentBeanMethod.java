package com.synaptix.component.factory;

import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import com.synaptix.component.IComponent;

public enum ComponentBeanMethod {

	COMPUTED_GET {
		@Override
		public boolean matches(Method method) {
			if (method.getAnnotation(IComponent.Computed.class) == null) {
				return false;
			}
			return GET.matches(method);
		}

		@Override
		public String inferName(Method method) {
			if (GET.matches(method)) {
				return GET.inferName(method);
			}
			return method.toGenericString();
		}
	},
	COMPUTED_SET {
		@Override
		public boolean matches(Method method) {
			if (method.getAnnotation(IComponent.Computed.class) == null) {
				return false;
			}
			return SET.matches(method);
		}

		@Override
		public String inferName(Method method) {
			if (SET.matches(method)) {
				return SET.inferName(method);
			}
			return method.toGenericString();
		}
	},
	GET {
		@Override
		public String inferName(Method method) {
			if (isBooleanProperty(method) && method.getName().startsWith(IS_PREFIX)) {
				return Introspector.decapitalize(method.getName().substring(2));
			}
			return super.inferName(method);
		}

		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length > 0) {
				return false;
			}

			if (isBooleanProperty(method)) {
				return true;
			}

			String name = method.getName();
			if ("getClass".equals(name)) {
				return false;
			}
			if (name.startsWith(GET_PREFIX) && name.length() > 3) {
				return true;
			}
			return false;
		}

		/**
		 * Returns {@code true} if the method matches {@code boolean isFoo()} or {@code boolean hasFoo()} property accessors.
		 */
		private boolean isBooleanProperty(Method method) {
			if (Boolean.class.equals(method.getReturnType()) || boolean.class.equals(method.getReturnType())) {
				String name = method.getName();
				if (name.startsWith(IS_PREFIX) && name.length() > 2) {
					return true;
				}
				if (name.startsWith(HAS_PREFIX) && name.length() > 3) {
					return true;
				}
			}
			return false;
		}
	},
	SET {
		@Override
		public boolean matches(Method method) {
			if (!void.class.equals(method.getReturnType())) {
				return false;
			}
			if (method.getParameterTypes().length != 1) {
				return false;
			}
			String name = method.getName();
			if (name.startsWith(SET_PREFIX) && name.length() > 3) {
				return true;
			}
			return false;
		}
	},
	TO_STRING {
		@Override
		public boolean matches(Method method) {
			if (!String.class.equals(method.getReturnType())) {
				return false;
			}
			if (method.getParameterTypes().length > 0) {
				return false;
			}
			String name = method.getName();
			if (name.equals("toString")) {
				return true;
			}
			return false;
		}
	},
	EQUALS {
		@Override
		public boolean matches(Method method) {
			if (!boolean.class.equals(method.getReturnType())) {
				return false;
			}
			if (method.getParameterTypes().length != 1) {
				return false;
			}
			if (!(Object.class.equals(method.getParameterTypes()[0]))) {
				return false;
			}
			String name = method.getName();
			if (name.equals("equals")) {
				return true;
			}
			return false;
		}
	},
	HASHCODE {
		@Override
		public boolean matches(Method method) {
			if (!int.class.equals(method.getReturnType())) {
				return false;
			}
			if (method.getParameterTypes().length > 0) {
				return false;
			}
			String name = method.getName();
			if (name.equals("hashCode")) {
				return true;
			}
			return false;
		}
	},
	ADD_PROPERTY_CHANGE_LISTENER {
		@Override
		public boolean matches(Method method) {
			if (!void.class.equals(method.getReturnType())) {
				return false;
			}
			if (method.getParameterTypes().length != 1) {
				return false;
			}
			if (!(PropertyChangeListener.class.equals(method.getParameterTypes()[0]))) {
				return false;
			}
			String name = method.getName();
			if (name.equals("addPropertyChangeListener")) {
				return true;
			}
			return false;
		}
	},
	ADD_PROPERTY_CHANGE_LISTENER_WITH_PROPERTY_NAME {
		@Override
		public boolean matches(Method method) {
			if (!void.class.equals(method.getReturnType())) {
				return false;
			}
			if (method.getParameterTypes().length != 2) {
				return false;
			}
			if (!(String.class.equals(method.getParameterTypes()[0]) && PropertyChangeListener.class.equals(method.getParameterTypes()[1]))) {
				return false;
			}
			String name = method.getName();
			if (name.equals("addPropertyChangeListener")) {
				return true;
			}
			return false;
		}
	},
	REMOVE_PROPERTY_CHANGE_LISTENER {
		@Override
		public boolean matches(Method method) {
			if (!void.class.equals(method.getReturnType())) {
				return false;
			}
			if (method.getParameterTypes().length != 1) {
				return false;
			}
			if (!(PropertyChangeListener.class.equals(method.getParameterTypes()[0]))) {
				return false;
			}
			String name = method.getName();
			if (name.equals("removePropertyChangeListener")) {
				return true;
			}
			return false;
		}
	},
	REMOVE_PROPERTY_CHANGE_LISTENER_WITH_PROPERTY_NAME {
		@Override
		public boolean matches(Method method) {
			if (!void.class.equals(method.getReturnType())) {
				return false;
			}
			if (method.getParameterTypes().length != 2) {
				return false;
			}
			if (!(String.class.equals(method.getParameterTypes()[0]) && PropertyChangeListener.class.equals(method.getParameterTypes()[1]))) {
				return false;
			}
			String name = method.getName();
			if (name.equals("removePropertyChangeListener")) {
				return true;
			}
			return false;
		}
	},
	STRAIGHT_GET_PROPERTIES {
		@Override
		public boolean matches(Method method) {
			if (!Map.class.equals(method.getReturnType())) {
				return false;
			}
			if (method.getParameterTypes().length != 0) {
				return false;
			}
			String name = method.getName();
			if (name.equals("straightGetProperties")) {
				return true;
			}
			return false;
		}
	},
	STRAIGHT_GET_PROPERTY {
		@Override
		public boolean matches(Method method) {
			if (!Object.class.equals(method.getReturnType())) {
				return false;
			}
			if (method.getParameterTypes().length != 1) {
				return false;
			}
			if (!String.class.equals(method.getParameterTypes()[0])) {
				return false;
			}
			String name = method.getName();
			if (name.equals("straightGetProperty")) {
				return true;
			}
			return false;
		}
	},
	STRAIGHT_GET_PROPERTY_NAMES {
		@Override
		public boolean matches(Method method) {
			if (!Set.class.equals(method.getReturnType())) {
				return false;
			}
			if (method.getParameterTypes().length != 0) {
				return false;
			}
			String name = method.getName();
			if (name.equals("straightGetPropertyNames")) {
				return true;
			}
			return false;
		}
	},
	STRAIGHT_GET_PROPERTY_CLASS {
		@Override
		public boolean matches(Method method) {
			if (!Class.class.equals(method.getReturnType())) {
				return false;
			}
			if (method.getParameterTypes().length != 1) {
				return false;
			}
			if (!String.class.equals(method.getParameterTypes()[0])) {
				return false;
			}
			String name = method.getName();
			if (name.equals("straightGetPropertyClass")) {
				return true;
			}
			return false;
		}
	},
	STRAIGHT_SET_PROPERTIES {
		@Override
		public boolean matches(Method method) {
			if (!void.class.equals(method.getReturnType())) {
				return false;
			}
			if (method.getParameterTypes().length != 1) {
				return false;
			}
			if (!Map.class.equals(method.getParameterTypes()[0])) {
				return false;
			}
			String name = method.getName();
			if (name.equals("straightSetProperties")) {
				return true;
			}
			return false;
		}
	},
	STRAIGHT_SET_PROPERTY {
		@Override
		public boolean matches(Method method) {
			if (!void.class.equals(method.getReturnType())) {
				return false;
			}
			if (method.getParameterTypes().length != 2) {
				return false;
			}
			if (!(String.class.equals(method.getParameterTypes()[0]) && Object.class.equals(method.getParameterTypes()[1]))) {
				return false;
			}
			String name = method.getName();
			if (name.equals("straightSetProperty")) {
				return true;
			}
			return false;
		}
	},
	CALL {
		/**
		 * Matches all leftover methods.
		 */
		@Override
		public boolean matches(Method method) {
			return true;
		}
	};

	private static final String GET_PREFIX = "get";

	private static final String HAS_PREFIX = "has";

	private static final String IS_PREFIX = "is";

	private static final String SET_PREFIX = "set";

	/**
	 * Determine which Action a method maps to.
	 */
	public static ComponentBeanMethod which(Method method) {
		if (method.isDefault()) {
			return CALL;
		}
		for (ComponentBeanMethod action : ComponentBeanMethod.values()) {
			if (action.matches(method)) {
				return action;
			}
		}
		throw new RuntimeException("CALL should have matched");
	}

	/**
	 * Infer the name of a property from the method.
	 */
	public String inferName(Method method) {
		if (this != GET && this != SET) {
			throw new UnsupportedOperationException("Cannot infer a property name for a CALL-type method");
		}
		return Introspector.decapitalize(method.getName().substring(3));
	}

	/**
	 * Returns {@code true} if the BeanLikeMethod matches the method.
	 */
	public abstract boolean matches(Method method);
}
