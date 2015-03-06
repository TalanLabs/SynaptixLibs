package com.synaptix.component.factory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

public class TypeHelper {

	public static final Class<?> findClass(Class<?> firstclazz, Class<?> lastClass, String name) {
		Class<?> res = null;

		List<Class<?>> clazzs = get(firstclazz, lastClass);
		if (clazzs != null && clazzs.size() >= 2) {
			int i = clazzs.size() - 2;
			while (i >= 0 && res == null) {
				Type type = findTypeArgument(clazzs.get(i), clazzs.get(i + 1), name);
				if (type instanceof Class) {
					res = (Class<?>) type;
				} else if (type instanceof TypeVariable) {
					TypeVariable<?> typeVariable = (TypeVariable<?>) type;
					name = typeVariable.getName();
				} else if (type instanceof ParameterizedType) {
					ParameterizedType parameterizedType = (ParameterizedType) type;
					res = (Class<?>) parameterizedType.getRawType();
				} else {
					System.out.println("erreur");
				}
				i--;
			}

		}
		return res;
	}

	private static List<Class<?>> get(Class<?> startClazz, Class<?> findClazz) {
		List<Class<?>> res = null;
		Class<?>[] interfaces = startClazz.getInterfaces();
		if (interfaces != null && interfaces.length > 0) {
			boolean ok = false;
			int i = 0;
			while (i < interfaces.length && !ok) {
				Class<?> inter = interfaces[i];
				if (inter.equals(findClazz)) {
					ok = true;
					res = new ArrayList<Class<?>>();
					res.add(startClazz);
					res.add(findClazz);
				} else {
					List<Class<?>> res2 = get(inter, findClazz);
					if (res2 != null) {
						ok = true;
						res = new ArrayList<Class<?>>();
						res.add(startClazz);
						res.addAll(res2);
					}
				}
				i++;
			}
		}
		return res;
	}

	private static Type findTypeArgument(Class<?> parentclazz, Class<?> childrenClass, String name) {
		Type res = null;
		Type type = findGenericInterfaces(parentclazz, childrenClass);
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			int pos = getGenericTypePosition(childrenClass, name);
			res = parameterizedType.getActualTypeArguments()[pos];
		}
		return res;
	}

	private static Type findGenericInterfaces(Class<?> parentClazz, Class<?> childrenClass) {
		Type res = null;
		int i = 0;
		while (i < parentClazz.getGenericInterfaces().length && res == null) {
			Type type = parentClazz.getGenericInterfaces()[i];
			if (equalsType(type, childrenClass)) {
				return type;
			}
			i++;
		}
		return res;
	}

	private static boolean equalsType(Type type, Class<?> clazz) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			return equalsType(parameterizedType.getRawType(), clazz);
		} else if (type instanceof Class) {
			Class<?> c = (Class<?>) type;
			return c.equals(clazz);
		} else {
			System.err.println("Type non traité " + type.getClass());
		}
		return false;
	}

	private static <T> int getGenericTypePosition(Class<T> clazz, String name) {
		int res = -1;
		int i = 0;
		while (i < clazz.getTypeParameters().length && res == -1) {
			TypeVariable<Class<T>> tv = clazz.getTypeParameters()[i];
			if (name.equals(tv.getName())) {
				res = i;
			}
			i++;
		}
		return res;
	}
}
