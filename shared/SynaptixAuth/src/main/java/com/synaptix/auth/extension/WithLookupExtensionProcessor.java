package com.synaptix.auth.extension;

import java.lang.reflect.Method;

import com.synaptix.auth.AbstractAuthExtensionProcessor;
import com.synaptix.auth.AuthsBundle;
import com.synaptix.auth.AuthsBundleManager;

public class WithLookupExtensionProcessor extends AbstractAuthExtensionProcessor {

	@Override
	public Object process(AuthsBundleManager authsBundleManager, Class<? extends AuthsBundle> bundleClass, Class<?> authExtensionClass, Method method, Object[] args) {
		if (authExtensionClass == IWithLookupExtension.class && method.getName().equals("hasAuth") && method.getReturnType().equals(boolean.class) && method.getParameterTypes().length == 2
				&& method.getParameterTypes()[0] == String.class && method.getParameterTypes()[1] == String.class) {
			String object = (String) args[0];
			String action = (String) args[1];
			return authsBundleManager.hasAuth(bundleClass, object, action);
		}
		return null;
	}
}
