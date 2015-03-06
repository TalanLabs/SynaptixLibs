package com.synaptix.auth;

import java.util.List;

/**
 * 
 * @author Gaby
 * 
 */
public abstract class AuthsBundleManager {

	private static AuthsBundleManager instance = new DefaultAuthsBundleManager();

	public static final void setInstance(AuthsBundleManager instance) {
		AuthsBundleManager.instance = instance;
	}

	public static final AuthsBundleManager getInstance() {
		return instance;
	}

	/**
	 * Test auth for object, action
	 * 
	 * @param bundleName
	 * @param object
	 * @param action
	 * @return
	 */
	public abstract boolean hasAuth(Class<? extends AuthsBundle> bundleClass, String object, String action);

	/**
	 * Add a auths bundle interface
	 * 
	 * @param bundleClass
	 */
	public abstract void addBundle(Class<? extends AuthsBundle> bundleClass);

	/**
	 * Get a instance of interface bundle
	 * 
	 * @param bundleClass
	 * @return
	 */
	public abstract <E extends AuthsBundle> E getBundle(Class<E> bundleClass);

	/**
	 * Get a all bundle class
	 * 
	 * @return
	 */
	public abstract List<Class<? extends AuthsBundle>> getAllBundleClasss();

	/**
	 * Add extension
	 * 
	 * @param authExtensionClass
	 * @param authExtensionProcessor
	 */
	public abstract void addExtension(Class<?> authExtensionClass, IAuthExtensionProcessor authExtensionProcessor);

	/**
	 * Get a processor for extension
	 * 
	 * @param authExtensionClass
	 * @return
	 */
	public abstract IAuthExtensionProcessor getExtension(Class<?> authExtensionClass);

	/**
	 * Get a all auth extension class
	 * 
	 * @return
	 */
	public abstract List<Class<?>> getAllAuthExtensionClasss();

}