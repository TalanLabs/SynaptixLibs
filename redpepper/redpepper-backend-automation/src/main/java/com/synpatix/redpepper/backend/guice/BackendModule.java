package com.synpatix.redpepper.backend.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.synpatix.redpepper.backend.core.runtime.RedPepperBackendFixture;
import com.synpatix.redpepper.backend.core.runtime.RepositorySetup;

public class BackendModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(RepositorySetup.class).in(Singleton.class);
		bind(RedPepperBackendFixture.class).in(Singleton.class);
	}

}
