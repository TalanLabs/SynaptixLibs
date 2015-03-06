package com.synaptix.auth;

import java.lang.reflect.Method;

public abstract class AbstractAuthExtensionProcessor implements IAuthExtensionProcessor {

	@Override
	public void addAuthInformation(AuthsBundleManager authsBundleManager, Class<? extends AuthsBundle> bundleClass, AuthInformation authInformation) {
	}

	@Override
	public Object process(AuthsBundleManager authsBundleManager, Class<? extends AuthsBundle> bundleClass, Class<?> authExtensionClass, Method method, Object[] args) {
		return null;
	}
}