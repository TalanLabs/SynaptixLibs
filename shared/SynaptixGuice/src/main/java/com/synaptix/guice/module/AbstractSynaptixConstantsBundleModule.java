package com.synaptix.guice.module;

import java.lang.annotation.Annotation;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.synaptix.constants.shared.ConstantsBundle;

public abstract class AbstractSynaptixConstantsBundleModule extends AbstractModule {

	private final Class<? extends Annotation> annotationType;

	public AbstractSynaptixConstantsBundleModule() {
		this(null);
	}

	public AbstractSynaptixConstantsBundleModule(Class<? extends Annotation> annotationType) {
		super();
		this.annotationType = annotationType;
	}

	/**
	 * Add a auths bundle
	 * 
	 * @param authsBundleClass
	 */
	public final <T extends ConstantsBundle> void bindConstantsBundle(Class<T> constantsBundleClass) {
		if (annotationType != null) {
			bind(constantsBundleClass).annotatedWith(annotationType).toProvider(new SynaptixConstantsBundleProvider<T>(annotationType, constantsBundleClass)).in(Scopes.SINGLETON);
		} else {
			bind(constantsBundleClass).toProvider(new SynaptixConstantsBundleProvider<T>(constantsBundleClass)).in(Scopes.SINGLETON);
		}
	}
}
