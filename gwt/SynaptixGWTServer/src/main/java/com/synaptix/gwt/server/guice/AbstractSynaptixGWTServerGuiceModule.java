package com.synaptix.gwt.server.guice;

import com.google.inject.AbstractModule;

public abstract class AbstractSynaptixGWTServerGuiceModule extends AbstractModule {

	@Override
	protected final void configure() {
		configureModule();
	}

	protected abstract void configureModule();

}
