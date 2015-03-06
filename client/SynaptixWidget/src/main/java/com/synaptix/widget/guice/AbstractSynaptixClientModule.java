package com.synaptix.widget.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.synaptix.widget.error.view.swing.IErrorInfoBuilder;

public abstract class AbstractSynaptixClientModule extends AbstractModule {

	private Multibinder<IErrorInfoBuilder> errorInfoBuilderMultibinder;

	private Multibinder<IErrorInfoBuilder> getErrorInfoBuilderMultibinder() {
		if (errorInfoBuilderMultibinder == null) {
			errorInfoBuilderMultibinder = Multibinder.newSetBinder(binder(), IErrorInfoBuilder.class);
		}
		return errorInfoBuilderMultibinder;
	}

	public void addErrorBuilder(Class<? extends IErrorInfoBuilder> builderClass) {
		getErrorInfoBuilderMultibinder().addBinding().to(builderClass).in(Singleton.class);
	}
}