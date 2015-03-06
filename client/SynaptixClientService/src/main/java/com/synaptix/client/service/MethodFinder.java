package com.synaptix.client.service;

import java.lang.reflect.Method;

public final class MethodFinder {
	
	private MethodFinder() {
		
	}
	
	public static Method findMethod(
			final Class<?> clazz, 
			final String methodName
			) throws SecurityException, NoSuchMethodException {
		if (clazz == null) {
			throw new NullPointerException("Class must not null"); //$NON-NLS-1$
		}
		if (methodName == null) {
			throw new NullPointerException("MethodName must not null"); //$NON-NLS-1$
		}
		return findMethod(clazz, methodName, null); //$NON-NLS-1$
	}
	
	public static Method findMethod(
			final Class<?> clazz, 
			final Class<?>[] parameterTypes
			) throws SecurityException, NoSuchMethodException {
		if (clazz == null) {
			throw new NullPointerException("Class must not null"); //$NON-NLS-1$
		}
		if (parameterTypes == null) {
			throw new NullPointerException("parameterTypes must not null"); //$NON-NLS-1$
		}
		return findMethod(clazz, null, parameterTypes);
	}
	
	public static Method findMethod(
			final Class<?> clazz, 
			final String methodName,
			final Class<?>[] parameterTypes
			) throws SecurityException, NoSuchMethodException {
		Method methodRet = null;
		if (parameterTypes == null) {
			final Method[] methods = clazz.getMethods();
			if (methods != null) {
				for (Method method : methods) {
					if(methodName.equals(method.getName())) {
						methodRet = method;
						break;
					}
				}
			}
		} 
		else {
			methodRet = clazz.getMethod(methodName, parameterTypes);
		}
		if(methodRet == null) {
			throw new IllegalArgumentException("MethodName : "+methodName+" does not exist in "+clazz); //$NON-NLS-1$
		}
		return methodRet;
	}
	
	
}
