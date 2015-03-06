package com.synaptix.auth.extension;

import java.lang.reflect.Method;

import com.synaptix.auth.AbstractAuthExtensionProcessor;
import com.synaptix.auth.AuthsBundle;
import com.synaptix.auth.AuthsBundleManager;

public class RootAuthExtensionProcessor extends AbstractAuthExtensionProcessor {

	private boolean root;

	public RootAuthExtensionProcessor() {
		this(false);
	}

	public RootAuthExtensionProcessor(boolean root) {
		super();
		this.root = root;
	}

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	@Override
	public Object process(AuthsBundleManager authsBundleManager, Class<? extends AuthsBundle> bundleClass, Class<?> authExtensionClass, Method method, Object[] args) {
		if (authExtensionClass == IRootAuthExtension.class && method.getName().equals("isRoot") && method.getReturnType().equals(boolean.class) && method.getParameterTypes().length == 0) {
			return root;
		}
		return null;
	}
}
