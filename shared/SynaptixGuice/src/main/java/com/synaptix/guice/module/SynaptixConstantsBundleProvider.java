package com.synaptix.guice.module;

import java.lang.annotation.Annotation;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.synaptix.constants.ConstantsBundleManager;
import com.synaptix.constants.shared.ConstantsBundle;

public class SynaptixConstantsBundleProvider<T extends ConstantsBundle> implements Provider<T> {

	private final Class<? extends Annotation> annotationType;

	private final Class<T> constantsBundleClass;

	private ConstantsBundleManager constantsBundleManager;

	public SynaptixConstantsBundleProvider(Class<T> constantsBundleClass) {
		this(null, constantsBundleClass);
	}

	public SynaptixConstantsBundleProvider(Class<? extends Annotation> annotationType, Class<T> constantsBundleClass) {
		super();

		this.annotationType = annotationType;
		this.constantsBundleClass = constantsBundleClass;
	}

	@Inject
	public void setInjector(Injector injector) {
		Key<ConstantsBundleManager> key;
		if (annotationType != null) {
			key = Key.get(ConstantsBundleManager.class, annotationType);
		} else {
			key = Key.get(ConstantsBundleManager.class);
		}
		constantsBundleManager = injector.getInstance(key);
		constantsBundleManager.addBundle(constantsBundleClass);
	}

	@Override
	public T get() {
		return constantsBundleManager.getBundle(constantsBundleClass);
	}
}
