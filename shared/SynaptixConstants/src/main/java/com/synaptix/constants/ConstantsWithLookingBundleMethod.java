package com.synaptix.constants;

import java.lang.reflect.Method;
import java.util.Map;

public enum ConstantsWithLookingBundleMethod {

	GET_STRING {

		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].equals(String.class)) {
				return false;
			}

			if (!method.getReturnType().equals(String.class)) {
				return false;
			}

			String name = method.getName();
			if (name.equals("getString")) {
				return true;
			}
			return false;
		}
	},
	GET_STRING_ARGS {
		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length < 1 || !method.getParameterTypes()[0].equals(String.class)) {
				return false;
			}

			if (!method.getReturnType().equals(String.class)) {
				return false;
			}

			String name = method.getName();
			if (name.equals("getString")) {
				return true;
			}
			return false;
		}
	},
	GET_INT {
		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].equals(String.class)) {
				return false;
			}

			if (!method.getReturnType().equals(Integer.class)) {
				return false;
			}

			String name = method.getName();
			if (name.equals("getInt")) {
				return true;
			}
			return false;
		}
	},
	GET_DOUBLE {
		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].equals(String.class)) {
				return false;
			}

			if (!method.getReturnType().equals(Double.class)) {
				return false;
			}

			String name = method.getName();
			if (name.equals("getDouble")) {
				return true;
			}
			return false;
		}
	},
	GET_FLOAT {
		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].equals(String.class)) {
				return false;
			}

			if (!method.getReturnType().equals(Float.class)) {
				return false;
			}

			String name = method.getName();
			if (name.equals("getFloat")) {
				return true;
			}
			return false;
		}
	},
	GET_BOOLEAN {
		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].equals(String.class)) {
				return false;
			}

			if (!method.getReturnType().equals(Boolean.class)) {
				return false;
			}

			String name = method.getName();
			if (name.equals("getBoolean")) {
				return true;
			}
			return false;
		}
	},
	GET_STRING_ARRAY {
		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].equals(String.class)) {
				return false;
			}

			if (!method.getReturnType().isArray() || !method.getReturnType().getComponentType().equals(String.class)) {
				return false;
			}

			String name = method.getName();
			if (name.equals("getStringArray")) {
				return true;
			}
			return false;
		}
	},
	GET_MAP {
		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].equals(String.class)) {
				return false;
			}

			if (!method.getReturnType().equals(Map.class)) {
				return false;
			}

			String name = method.getName();
			if (name.equals("getMap")) {
				return true;
			}
			return false;
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
	public static ConstantsWithLookingBundleMethod which(Method method) {
		for (ConstantsWithLookingBundleMethod action : ConstantsWithLookingBundleMethod.values()) {
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
