package com.synaptix.auth;

import java.lang.reflect.Method;

/* package protected */enum AuthsBundleMethod {

	CALL_BOOLEAN {
		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length != 0) {
				return false;
			}
			if (!method.getReturnType().equals(boolean.class) && !method.getReturnType().equals(Boolean.class)) {
				return false;
			}
			if (method.getAnnotation(AuthsBundle.Key.class) == null) {
				return false;
			}
			return true;
		}
	},
	OTHER {
		@Override
		public boolean matches(Method method) {
			return true;
		}
	};

	/**
	 * Determine which Action a method maps to.
	 */
	public static AuthsBundleMethod which(Method method) {
		for (AuthsBundleMethod action : AuthsBundleMethod.values()) {
			if (action.matches(method)) {
				return action;
			}
		}
		throw new RuntimeException("OTHER should have matched");
	}

	/**
	 * Returns {@code true} if the MessageBundleMethod matches the method.
	 */
	public abstract boolean matches(Method method);
}
