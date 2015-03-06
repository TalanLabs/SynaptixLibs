package com.synaptix.constants;

import java.lang.reflect.Method;
import java.util.Map;

import com.synaptix.constants.shared.ConstantsBundle;

public enum ConstantsBundleMethod {

	CALL_STRING {
		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length != 0) {
				return false;
			}
			if (!method.getReturnType().equals(String.class)) {
				return false;
			}
			return true;
		}
	},
	CALL_STRING_ARGS {
		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length <= 0) {
				return false;
			}
			if (!method.getReturnType().equals(String.class)) {
				return false;
			}
			return true;
		}
	},
	CALL_BOOLEAN {
		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length != 0) {
				return false;
			}
			if (!method.getReturnType().equals(boolean.class) && !method.getReturnType().equals(Boolean.class)) {
				return false;
			}
			return true;
		}
	},
	CALL_INT {
		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length != 0) {
				return false;
			}
			if (!method.getReturnType().equals(int.class) && !method.getReturnType().equals(Integer.class)) {
				return false;
			}
			return true;
		}

	},
	CALL_FLOAT {
		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length != 0) {
				return false;
			}
			if (!method.getReturnType().equals(float.class) && !method.getReturnType().equals(Float.class)) {
				return false;
			}
			return true;
		}

	},
	CALL_DOUBLE {
		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length != 0) {
				return false;
			}
			if (!method.getReturnType().equals(double.class) && !method.getReturnType().equals(Double.class)) {
				return false;
			}
			return true;
		}

	},
	CALL_STRINGARRAY {
		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length != 0) {
				return false;
			}
			if (!method.getReturnType().isArray() || !method.getReturnType().getComponentType().equals(String.class)) {
				return false;
			}
			return true;
		}

	},
	CALL_MAP {
		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length != 0) {
				return false;
			}
			if (!method.getReturnType().equals(Map.class)) {
				return false;
			}
			return true;
		}

	},
	CALL_CONSTANTS_BUNDLE {
		@Override
		public boolean matches(Method method) {
			if (method.getParameterTypes().length != 0) {
				return false;
			}
			if (!ConstantsBundle.class.isAssignableFrom(method.getReturnType())) {
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
	public static ConstantsBundleMethod which(Method method) {
		for (ConstantsBundleMethod action : ConstantsBundleMethod.values()) {
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
