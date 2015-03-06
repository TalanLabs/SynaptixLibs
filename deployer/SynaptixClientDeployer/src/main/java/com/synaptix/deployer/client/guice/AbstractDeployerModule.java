package com.synaptix.deployer.client.guice;

import java.util.List;

import org.apache.commons.logging.LogFactory;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.synaptix.deployer.client.controller.IDatabaseQueryContext;
import com.synaptix.deployer.environment.ISynaptixEnvironment;
import com.synaptix.deployer.job.ISynaptixJob;
import com.synaptix.deployer.model.ISynaptixDatabaseSchema;
import com.synaptix.deployer.model.SynaptixDatabase;

public abstract class AbstractDeployerModule extends AbstractModule {

	private Multibinder<ISynaptixEnvironment> synaptixEnvironmentSet;

	private Multibinder<ISynaptixJob> synaptixJobSet;

	private Multibinder<SynaptixDatabase> synaptixDatabaseSet;

	private Multibinder<IDatabaseQueryContext<?>> databaseQueryContextBinder;

	private MapBinder<Class<? extends ISynaptixEnvironment>, List<ISynaptixDatabaseSchema>> environmentDatabaseBinding;

	@Override
	protected final void configure() {
		this.synaptixEnvironmentSet = Multibinder.newSetBinder(binder(), ISynaptixEnvironment.class);
		this.synaptixJobSet = Multibinder.newSetBinder(binder(), ISynaptixJob.class);
		this.synaptixDatabaseSet = Multibinder.newSetBinder(binder(), SynaptixDatabase.class);
		this.environmentDatabaseBinding = MapBinder.newMapBinder(binder(), new TypeLiteral<Class<? extends ISynaptixEnvironment>>() {
		}, new TypeLiteral<List<ISynaptixDatabaseSchema>>() {
		});

		configureDeployer();
	}

	protected abstract void configureDeployer();

	protected final EnvironmentBuilder bindEnvironment(final Class<? extends ISynaptixEnvironment> synaptixEnvironmentClass) {
		synaptixEnvironmentSet.addBinding().to(synaptixEnvironmentClass);
		return new EnvironmentBuilder() {

			@Override
			public void withDB(Class<? extends SynaptixDatabase> databaseClass) {
				bindDB(databaseClass);
				try {
					environmentDatabaseBinding.addBinding(synaptixEnvironmentClass).toInstance(databaseClass.newInstance().getDbs());
				} catch (Exception e) {
					LogFactory.getLog(AbstractDeployerModule.class).error("DB INSTANCE", e);
				}
			}
		};
	}

	public interface EnvironmentBuilder {

		public void withDB(Class<? extends SynaptixDatabase> databaseClass);
	}

	protected final void bindJob(Class<? extends ISynaptixJob> synaptixJobClass) {
		synaptixJobSet.addBinding().to(synaptixJobClass);
	}

	protected final void bindDB(Class<? extends SynaptixDatabase> databaseClass) {
		synaptixDatabaseSet.addBinding().to(databaseClass);

		requestStaticInjection(databaseClass);
	}

	/**
	 * Adds a database query checker context so that it is available in the "database checker view" and can be tested
	 * 
	 * @param databaseQueryContextClass
	 */
	protected final void addDatabaseQueryCheckerContext(Class<? extends IDatabaseQueryContext<?>> databaseQueryContextClass) {
		if (databaseQueryContextBinder == null) {
			databaseQueryContextBinder = Multibinder.newSetBinder(binder(), new TypeLiteral<IDatabaseQueryContext<?>>() {
			});
		}
		databaseQueryContextBinder.addBinding().to(databaseQueryContextClass);
	}
}
