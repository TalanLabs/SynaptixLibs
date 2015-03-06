package com.synaptix.auth.annotation.helper;

import javax.lang.model.element.ExecutableElement;

import com.synaptix.auth.AuthsBundle;

public enum AuthBeanMethod {

	CALL_BOOLEAN {
		@Override
		public boolean matches(ExecutableElement method) {
			if (method.getParameters().size() != 0) {
				return false;
			}

			if (!method.getReturnType().toString().equals("java.lang.Boolean") && !method.getReturnType().toString().equals("boolean")) {
				return false;
			}

			if (method.getAnnotation(AuthsBundle.Key.class) == null) {
				return false;
			}
			return true;
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

	/**
	 * Determine which Action a method maps to.
	 */
	public static AuthBeanMethod which(ExecutableElement method) {
		for (AuthBeanMethod action : AuthBeanMethod.values()) {
			if (action.matches(method)) {
				return action;
			}
		}
		throw new RuntimeException("CALL should have matched");
	}

	/**
	 * Returns {@code true} if the BeanLikeMethod matches the method.
	 */
	public abstract boolean matches(ExecutableElement method);
}
