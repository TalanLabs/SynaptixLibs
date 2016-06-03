package com.synaptix.component.factory;

import java.util.Arrays;

public class PrimitiveHelper {

	public static Object determineValue(Class<?> type) {
		if (byte.class.equals(type)) {
			return new Byte((byte) 0);
		} else if (short.class.equals(type)) {
			return new Short((short) 0);
		} else if (int.class.equals(type)) {
			return new Integer(0);
		} else if (long.class.equals(type)) {
			return new Long(0);
		} else if (float.class.equals(type)) {
			return new Float(0);
		} else if (double.class.equals(type)) {
			return new Double(0);
		} else if (char.class.equals(type)) {
			return new Character(' ');
		} else if (boolean.class.equals(type)) {
			return new Boolean(false);
		} else {
			return null;
		}
	}

	public static Class<?> determineClass(Class<?> type) {
		if (byte.class.equals(type)) {
			return Byte.class;
		} else if (short.class.equals(type)) {
			return Short.class;
		} else if (int.class.equals(type)) {
			return Integer.class;
		} else if (long.class.equals(type)) {
			return Long.class;
		} else if (float.class.equals(type)) {
			return Float.class;
		} else if (double.class.equals(type)) {
			return Double.class;
		} else if (char.class.equals(type)) {
			return Character.class;
		} else if (boolean.class.equals(type)) {
			return Boolean.class;
		} else if (void.class.equals(type)) {
			return Void.class;
		} else {
			return type;
		}
	}

	public static int arrayHashCode(Class<?> type, Object value) {
		Class<?> clazz = type.getComponentType();
		if (clazz.isPrimitive()) {
			if (clazz == int.class) {
				return Arrays.hashCode((int[]) value);
			} else if (clazz == byte.class) {
				return Arrays.hashCode((byte[]) value);
			} else if (clazz == short.class) {
				return Arrays.hashCode((short[]) value);
			} else if (clazz == long.class) {
				return Arrays.hashCode((long[]) value);
			} else if (clazz == float.class) {
				return Arrays.hashCode((float[]) value);
			} else if (clazz == double.class) {
				return Arrays.hashCode((double[]) value);
			} else if (clazz == char.class) {
				return Arrays.hashCode((char[]) value);
			} else if (clazz == boolean.class) {
				return Arrays.hashCode((boolean[]) value);
			} else {
				return 1;
			}
		} else {
			return Arrays.deepHashCode((Object[]) value);
		}
	}

	public static boolean arrayEquals(Class<?> type, Object value1, Object value2) {
		Class<?> clazz = type.getComponentType();
		if (clazz.isPrimitive()) {
			if (clazz == int.class) {
				return Arrays.equals((int[]) value1, (int[]) value2);
			} else if (clazz == byte.class) {
				return Arrays.equals((byte[]) value1, (byte[]) value2);
			} else if (clazz == short.class) {
				return Arrays.equals((short[]) value1, (short[]) value2);
			} else if (clazz == long.class) {
				return Arrays.equals((long[]) value1, (long[]) value2);
			} else if (clazz == float.class) {
				return Arrays.equals((float[]) value1, (float[]) value2);
			} else if (clazz == double.class) {
				return Arrays.equals((double[]) value1, (double[]) value2);
			} else if (clazz == char.class) {
				return Arrays.equals((char[]) value1, (char[]) value2);
			} else if (clazz == boolean.class) {
				return Arrays.equals((boolean[]) value1, (boolean[]) value2);
			} else {
				return false;
			}
		} else {
			return Arrays.deepEquals((Object[]) value1, (Object[]) value2);
		}
	}
}
