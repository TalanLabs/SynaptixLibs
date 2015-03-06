package com.synaptix.gwt.rebind.constants;

import java.util.Map;

import com.google.gwt.core.ext.typeinfo.JMethod;

/* package protected */enum ConstantsWithLookingGWTBundleMethod {

	GET_STRING {

		@Override
		public boolean matches(JMethod method) {
			if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].getQualifiedSourceName().equals(String.class.getName())) {
				return false;
			}

			if (!method.getReturnType().getQualifiedSourceName().equals(String.class.getName())) {
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
		public boolean matches(JMethod method) {
			if (method.getParameterTypes().length < 1 || !method.getParameterTypes()[0].getQualifiedSourceName().equals(String.class.getName())) {
				return false;
			}

			if (!method.getReturnType().getQualifiedSourceName().equals(String.class.getName())) {
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
		public boolean matches(JMethod method) {
			if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].getQualifiedSourceName().equals(String.class.getName())) {
				return false;
			}

			if (!method.getReturnType().getQualifiedSourceName().equals(Integer.class.getName())) {
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
		public boolean matches(JMethod method) {
			if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].getQualifiedSourceName().equals(String.class.getName())) {
				return false;
			}

			if (!method.getReturnType().getQualifiedSourceName().equals(Double.class.getName())) {
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
		public boolean matches(JMethod method) {
			if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].getQualifiedSourceName().equals(String.class.getName())) {
				return false;
			}

			if (!method.getReturnType().getQualifiedSourceName().equals(Float.class.getName())) {
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
		public boolean matches(JMethod method) {
			if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].getQualifiedSourceName().equals(String.class.getName())) {
				return false;
			}

			if (!method.getReturnType().getQualifiedSourceName().equals(Boolean.class.getName())) {
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
		public boolean matches(JMethod method) {
			if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].getQualifiedSourceName().equals(String.class.getName())) {
				return false;
			}

			if (method.getReturnType().isArray() == null || !method.getReturnType().isArray().getComponentType().getQualifiedSourceName().equals(String.class.getName())) {
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
		public boolean matches(JMethod method) {
			if (method.getParameterTypes().length != 1 || !method.getParameterTypes()[0].getQualifiedSourceName().equals(String.class.getName())) {
				return false;
			}

			if (!method.getReturnType().getQualifiedSourceName().equals(Map.class.getName())) {
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
		public boolean matches(JMethod method) {
			return true;
		}

	};

	/**
	 * Determine which Action a method maps to.
	 */
	public static ConstantsWithLookingGWTBundleMethod which(JMethod method) {
		for (ConstantsWithLookingGWTBundleMethod action : ConstantsWithLookingGWTBundleMethod.values()) {
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
