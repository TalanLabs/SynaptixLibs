package com.synaptix.auth;

import java.lang.reflect.Method;

public interface IAuthExtensionProcessor {

	/**
	 * 
	 * @param authsBundleManager
	 * @param bundleClass
	 * @param authInformation
	 */
	public void addAuthInformation(AuthsBundleManager authsBundleManager, Class<? extends AuthsBundle> bundleClass, AuthInformation authInformation);

	/**
	 * Execute a method
	 * 
	 * @param authsBundleManager
	 * @param authsBundleClass
	 * @param authExtensionClass
	 * @param method
	 * @param args
	 * @return
	 */
	public Object process(AuthsBundleManager authsBundleManager, Class<? extends AuthsBundle> bundleClass, Class<?> authExtensionClass, Method method, Object[] args);

}
