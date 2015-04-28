package com.synaptix.guice.module;

import java.lang.annotation.Annotation;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.synaptix.constants.ConstantsBundleManager;
import com.synaptix.constants.shared.ConstantsBundle;

public class SynaptixConstantsBundleProvider<T extends ConstantsBundle> implements Provider<T> {

	private final Key<ConstantsBundleManager> key;

	private final Class<T> constantsBundleClass;

	private ConstantsBundleManager constantsBundleManager;

	public SynaptixConstantsBundleProvider(Class<T> constantsBundleClass) {
		super();

		this.key = Key.get(ConstantsBundleManager.class);
		this.constantsBundleClass = constantsBundleClass;
	}

	public SynaptixConstantsBundleProvider(Class<? extends Annotation> annotationType, Class<T> constantsBundleClass) {
		super();

		this.key = Key.get(ConstantsBundleManager.class, annotationType);
		this.constantsBundleClass = constantsBundleClass;
	}

	public SynaptixConstantsBundleProvider(Annotation annotation, Class<T> constantsBundleClass) {
		super();

		this.key = Key.get(ConstantsBundleManager.class, annotation);
		this.constantsBundleClass = constantsBundleClass;
	}

	@Inject
	public void setInjector(Injector injector) {
		constantsBundleManager = injector.getInstance(key);
		constantsBundleManager.addBundle(constantsBundleClass);
	}

	@Override
	public T get() {
		return constantsBundleManager.getBundle(constantsBundleClass);
	}
}
