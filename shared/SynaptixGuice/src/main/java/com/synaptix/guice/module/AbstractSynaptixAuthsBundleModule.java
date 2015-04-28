package com.synaptix.guice.module;

import java.lang.annotation.Annotation;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.synaptix.auth.AuthsBundle;

public abstract class AbstractSynaptixAuthsBundleModule extends AbstractModule {

	private final Class<? extends Annotation> annotationType;

	private final Annotation annotation;

	public AbstractSynaptixAuthsBundleModule() {
		super();

		this.annotationType = null;
		this.annotation = null;
	}

	public AbstractSynaptixAuthsBundleModule(Class<? extends Annotation> annotationType) {
		super();

		this.annotationType = annotationType;
		this.annotation = null;
	}

	public AbstractSynaptixAuthsBundleModule(Annotation annotation) {
		super();

		this.annotationType = null;
		this.annotation = annotation;
	}

	/**
	 * Add a auths bundle
	 * 
	 * @param authsBundleClass
	 */
	protected final <T extends AuthsBundle> void bindAuthsBundle(Class<T> authsBundleClass) {
		if (annotationType != null) {
			bind(authsBundleClass).annotatedWith(annotationType).toProvider(new SynaptixAuthsBundleProvider<T>(annotationType, authsBundleClass)).in(Scopes.SINGLETON);
		} else if (annotation != null) {
			bind(authsBundleClass).annotatedWith(annotation).toProvider(new SynaptixAuthsBundleProvider<T>(annotationType, authsBundleClass)).in(Scopes.SINGLETON);
		} else {
			bind(authsBundleClass).toProvider(new SynaptixAuthsBundleProvider<T>(authsBundleClass)).in(Scopes.SINGLETON);
		}
	}

}
