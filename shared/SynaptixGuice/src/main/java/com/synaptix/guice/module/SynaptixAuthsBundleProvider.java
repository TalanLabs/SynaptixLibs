package com.synaptix.guice.module;

import java.lang.annotation.Annotation;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.synaptix.auth.AuthsBundle;
import com.synaptix.auth.AuthsBundleManager;

public class SynaptixAuthsBundleProvider<T extends AuthsBundle> implements Provider<T> {

	private final Key<AuthsBundleManager> key;

	private final Class<T> authsBundleClass;

	private AuthsBundleManager authsBundleManager;

	public SynaptixAuthsBundleProvider(Class<T> authsBundleClass) {
		super();

		this.key = Key.get(AuthsBundleManager.class);
		this.authsBundleClass = authsBundleClass;
	}

	public SynaptixAuthsBundleProvider(Class<? extends Annotation> annotationType, Class<T> authsBundleClass) {
		super();

		this.key = Key.get(AuthsBundleManager.class, annotationType);
		this.authsBundleClass = authsBundleClass;
	}

	public SynaptixAuthsBundleProvider(Annotation annotation, Class<T> authsBundleClass) {
		super();

		this.key = Key.get(AuthsBundleManager.class, annotation);
		this.authsBundleClass = authsBundleClass;
	}

	@Inject
	public void setInjector(Injector injector) {
		authsBundleManager = injector.getInstance(key);
		authsBundleManager.addBundle(authsBundleClass);
	}

	@Override
	public T get() {
		return authsBundleManager.getBundle(authsBundleClass);
	}
}
