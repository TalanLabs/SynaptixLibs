package com.synaptix.gwt.rebind.constants;

import java.util.Map;

import com.google.gwt.core.ext.typeinfo.JMethod;

/* package protected */enum ConstantsGWTBundleMethod {

	CALL_STRING {
		@Override
		public boolean matches(JMethod method) {
			if (method.getParameterTypes().length != 0) {
				return false;
			}
			if (!method.getReturnType().getQualifiedSourceName().equals(String.class.getName())) {
				return false;
			}
			return true;
		}
	},
	CALL_STRING_ARGS {
		@Override
		public boolean matches(JMethod method) {
			if (method.getParameterTypes().length <= 0) {
				return false;
			}
			if (!method.getReturnType().getQualifiedSourceName().equals(String.class.getName())) {
				return false;
			}
			return true;
		}
	},
	CALL_BOOLEAN {
		@Override
		public boolean matches(JMethod method) {
			if (method.getParameterTypes().length != 0) {
				return false;
			}
			if (!method.getReturnType().getQualifiedSourceName().equals(boolean.class.getName()) && !method.getReturnType().getQualifiedSourceName().equals(Boolean.class.getName())) {
				return false;
			}
			return true;
		}
	},
	CALL_INT {
		@Override
		public boolean matches(JMethod method) {
			if (method.getParameterTypes().length != 0) {
				return false;
			}
			if (!method.getReturnType().getQualifiedSourceName().equals(int.class.getName()) && !method.getReturnType().getQualifiedSourceName().equals(Integer.class.getName())) {
				return false;
			}
			return true;
		}

	},
	CALL_FLOAT {
		@Override
		public boolean matches(JMethod method) {
			if (method.getParameterTypes().length != 0) {
				return false;
			}
			if (!method.getReturnType().getQualifiedSourceName().equals(float.class.getName()) && !method.getReturnType().getQualifiedSourceName().equals(Float.class.getName())) {
				return false;
			}
			return true;
		}

	},
	CALL_DOUBLE {
		@Override
		public boolean matches(JMethod method) {
			if (method.getParameterTypes().length != 0) {
				return false;
			}
			if (!method.getReturnType().getQualifiedSourceName().equals(double.class.getName()) && !method.getReturnType().getQualifiedSourceName().equals(Double.class.getName())) {
				return false;
			}
			return true;
		}

	},
	CALL_STRINGARRAY {
		@Override
		public boolean matches(JMethod method) {
			if (method.getParameterTypes().length != 0) {
				return false;
			}
			if (method.getReturnType().isArray() == null || !method.getReturnType().isArray().getComponentType().getQualifiedSourceName().equals(String.class.getName())) {
				return false;
			}
			return true;
		}

	},
	CALL_MAP {
		@Override
		public boolean matches(JMethod method) {
			if (method.getParameterTypes().length != 0) {
				return false;
			}
			if (!method.getReturnType().getQualifiedSourceName().equals(Map.class.getName())) {
				return false;
			}
			return true;
		}

	},
	OTHER {
		@Override
		public boolean matches(JMethod method) {
			return true;
		}
	};

	/**
	 * Determine which Action a method maps to.
	 */
	public static ConstantsGWTBundleMethod which(JMethod method) {
		for (ConstantsGWTBundleMethod action : ConstantsGWTBundleMethod.values()) {
			if (action.matches(method)) {
				return action;
			}
		}
		throw new RuntimeException("OTHER should have matched");
	}

	/**
	 * Returns {@code true} if the MessageBundleJMethod matches the method.
	 */
	public abstract boolean matches(JMethod method);
}
