package com.synaptix.guice.module;

import java.lang.annotation.Annotation;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.synaptix.constants.shared.ConstantsBundle;

public abstract class AbstractSynaptixConstantsBundleModule extends AbstractModule {

	private final Class<? extends Annotation> annotationType;

	private final Annotation annotation;

	public AbstractSynaptixConstantsBundleModule() {
		super();

		this.annotationType = null;
		this.annotation = null;
	}

	public AbstractSynaptixConstantsBundleModule(Class<? extends Annotation> annotationType) {
		super();

		this.annotationType = annotationType;
		this.annotation = null;
	}

	public AbstractSynaptixConstantsBundleModule(Annotation annotation) {
		super();

		this.annotationType = null;
		this.annotation = annotation;
	}

	/**
	 * Add a auths bundle
	 * 
	 * @param authsBundleClass
	 */
	public final <T extends ConstantsBundle> void bindConstantsBundle(Class<T> constantsBundleClass) {
		if (annotationType != null) {
			bind(constantsBundleClass).annotatedWith(annotationType).toProvider(new SynaptixConstantsBundleProvider<T>(annotationType, constantsBundleClass)).in(Scopes.SINGLETON);
		} else if (annotation != null) {
			bind(constantsBundleClass).annotatedWith(annotation).toProvider(new SynaptixConstantsBundleProvider<T>(annotation, constantsBundleClass)).in(Scopes.SINGLETON);
		} else {
			bind(constantsBundleClass).toProvider(new SynaptixConstantsBundleProvider<T>(constantsBundleClass)).in(Scopes.SINGLETON);
		}
	}
}
