package com.synaptix.guice.module;

import java.lang.annotation.Annotation;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.synaptix.auth.AuthsBundle;
import com.synaptix.auth.AuthsBundleManager;

public class SynaptixAuthsBundleProvider<T extends AuthsBundle> implements Provider<T> {

	private final Class<? extends Annotation> annotationType;

	private final Class<T> authsBundleClass;

	private AuthsBundleManager authsBundleManager;

	public SynaptixAuthsBundleProvider(Class<T> authsBundleClass) {
		this(null, authsBundleClass);
	}

	public SynaptixAuthsBundleProvider(Class<? extends Annotation> annotationType, Class<T> authsBundleClass) {
		super();

		this.annotationType = annotationType;
		this.authsBundleClass = authsBundleClass;
	}

	@Inject
	public void setInjector(Injector injector) {
		Key<AuthsBundleManager> key;
		if (annotationType != null) {
			key = Key.get(AuthsBundleManager.class, annotationType);
		} else {
			key = Key.get(AuthsBundleManager.class);
		}
		authsBundleManager = injector.getInstance(key);
		authsBundleManager.addBundle(authsBundleClass);
	}

	@Override
	public T get() {
		return authsBundleManager.getBundle(authsBundleClass);
	}
}
