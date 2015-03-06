package com.synaptix.auth.helper;

import com.synaptix.auth.AuthsBundleManager;
import com.synaptix.auth.method.IAuthMethod;

public class DefaultAuth implements IAuth {

	private final IAuthMethod authMethod;

	public DefaultAuth(IAuthMethod authMethod) {
		super();
		this.authMethod = authMethod;
	}

	@Override
	public boolean hasAuth(AuthsBundleManager authsBundleManager) {
		return authsBundleManager.hasAuth(authMethod.getBundleClass(), authMethod.getObject(), authMethod.getAction());
	}

}
