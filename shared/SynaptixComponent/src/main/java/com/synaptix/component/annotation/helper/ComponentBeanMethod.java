package com.synaptix.component.annotation.helper;

import java.beans.Introspector;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;

import com.synaptix.component.IComponent;

public enum ComponentBeanMethod {

	COMPUTED_GET {
		@Override
		public boolean matches(ExecutableElement method) {
			if (method.getAnnotation(IComponent.Computed.class) == null) {
				return false;
			}
			return GET.matches(method);
		}

		@Override
		public String inferName(ExecutableElement method) {
			if (GET.matches(method)) {
				return GET.inferName(method);
			}
			return super.inferName(method);
		}
	},
	COMPUTED_SET {
		@Override
		public boolean matches(ExecutableElement method) {
			if (method.getAnnotation(IComponent.Computed.class) == null) {
				return false;
			}
			return SET.matches(method);
		}

		@Override
		public String inferName(ExecutableElement method) {
			if (SET.matches(method)) {
				return SET.inferName(method);
			}
			return super.inferName(method);
		}
	},
	GET {
		@Override
		public String inferName(ExecutableElement method) {
			if (isBooleanProperty(method) && method.getSimpleName().toString().startsWith(IS_PREFIX)) {
				return Introspector.decapitalize(method.getSimpleName().toString().substring(2));
			}
			return super.inferName(method);
		}

		@Override
		public boolean matches(ExecutableElement method) {
			if (method.getParameters().size() > 0) {
				return false;
			}

			if (isBooleanProperty(method)) {
				return true;
			}

			String name = method.getSimpleName().toString();
			if ("getClass".equals(name)) {
				return false;
			}
			if (name.startsWith(GET_PREFIX) && name.length() > 3) {
				return true;
			}
			return false;
		}
	},
	SET {
		@Override
		public boolean matches(ExecutableElement method) {
			if (!method.getReturnType().getKind().equals(TypeKind.VOID)) {
				return false;
			}
			if (method.getParameters().size() != 1) {
				return false;
			}
			String name = method.getSimpleName().toString();
			if (name.startsWith(SET_PREFIX) && name.length() > 3) {
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
		public boolean matches(ExecutableElement method) {
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
	public static ComponentBeanMethod which(ExecutableElement method) {
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
	public String inferName(ExecutableElement method) {
		if (this != GET && this != SET) {
			throw new UnsupportedOperationException("Cannot infer a property name for a CALL-type method");
		}
		return Introspector.decapitalize(method.getSimpleName().toString().substring(3));
	}

	/**
	 * Returns {@code true} if the BeanLikeMethod matches the method.
	 */
	public abstract boolean matches(ExecutableElement method);

	/**
	 * Returns {@code true} if the method matches {@code boolean isFoo()} or {@code boolean hasFoo()} property accessors.
	 */
	private static boolean isBooleanProperty(ExecutableElement method) {
		if (method.getReturnType().toString().equals("java.lang.Boolean") || method.getReturnType().toString().equals("boolean")) {
			String name = method.getSimpleName().toString();
			if (name.startsWith(IS_PREFIX) && name.length() > 2) {
				return true;
			}
			if (name.startsWith(HAS_PREFIX) && name.length() > 3) {
				return true;
			}
		}
		return false;
	}
}
