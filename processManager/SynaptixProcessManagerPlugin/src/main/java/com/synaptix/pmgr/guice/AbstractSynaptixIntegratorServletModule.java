package com.synaptix.pmgr.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.pmgr.core.lib.ProcessingChannel.Agent;
import com.synaptix.pmgr.model.ICronProcessDefinition;
import com.synaptix.pmgr.model.IExportProcessDefinition;
import com.synaptix.pmgr.model.IHeartbeatProcessDefinition;
import com.synaptix.pmgr.model.IImportProcessDefinition;
import com.synaptix.pmgr.model.ISimpleProcessDefinition;
import com.synaptix.pmgr.model.ProcessType;
import com.synaptix.pmgr.trigger.injector.IInjector;

public abstract class AbstractSynaptixIntegratorServletModule extends AbstractModule {

	private Multibinder<ISimpleProcessDefinition> simpleProcessDefinitionMultibinder;

	private Multibinder<IHeartbeatProcessDefinition> heartbeatProcessDefinitionMultibinder;

	private Multibinder<IImportProcessDefinition<?>> importProcessDefinitionMultibinder;

	private Multibinder<IExportProcessDefinition<?>> exportProcessDefinitionMultibinder;

	private Multibinder<ICronProcessDefinition> cronProcessDefinitionMultibinder;

	protected final Multibinder<ISimpleProcessDefinition> getSimpleProcessDefinitionMultibinder() {
		if (simpleProcessDefinitionMultibinder == null) {
			simpleProcessDefinitionMultibinder = Multibinder.newSetBinder(binder(), ISimpleProcessDefinition.class);
		}
		return simpleProcessDefinitionMultibinder;
	}

	protected final Multibinder<IImportProcessDefinition<?>> getImportProcessDefinitionMultibinder() {
		if (importProcessDefinitionMultibinder == null) {
			importProcessDefinitionMultibinder = Multibinder.newSetBinder(binder(), new TypeLiteral<IImportProcessDefinition<?>>() {
			});
		}
		return importProcessDefinitionMultibinder;
	}

	protected final Multibinder<IExportProcessDefinition<?>> getExportProcessDefinitionMultibinder() {
		if (exportProcessDefinitionMultibinder == null) {
			exportProcessDefinitionMultibinder = Multibinder.newSetBinder(binder(), new TypeLiteral<IExportProcessDefinition<?>>() {
			});
		}
		return exportProcessDefinitionMultibinder;
	}

	protected final Multibinder<IHeartbeatProcessDefinition> getHeartbeatProcessDefinitionMultibinder() {
		if (heartbeatProcessDefinitionMultibinder == null) {
			heartbeatProcessDefinitionMultibinder = Multibinder.newSetBinder(binder(), IHeartbeatProcessDefinition.class);
			heartbeatProcessDefinitionMultibinder.permitDuplicates();
		}
		return heartbeatProcessDefinitionMultibinder;
	}

	protected final Multibinder<ICronProcessDefinition> getCronProcessDefinitionMultibinder() {
		if (cronProcessDefinitionMultibinder == null) {
			cronProcessDefinitionMultibinder = Multibinder.newSetBinder(binder(), ICronProcessDefinition.class);
		}
		return cronProcessDefinitionMultibinder;
	}

	protected final <A extends Agent> void bindAgent(final Class<A> agentClass, final int maxWorking, final int maxWaiting) {
		bindAgent(agentClass, maxWorking, maxWaiting, ProcessType.SIMPLE_PROCESSING);
	}

	protected final <A extends Agent> void bindAgent(final Class<A> agentClass, final int maxWorking, final int maxWaiting, final ProcessType processType) {
		ISimpleProcessDefinition simpleProcessDefinition = ComponentFactory.getInstance().createInstance(ISimpleProcessDefinition.class);
		simpleProcessDefinition.setName(agentClass.getSimpleName());
		simpleProcessDefinition.setAgentClass(agentClass);
		simpleProcessDefinition.setMaxWorking(maxWorking);
		simpleProcessDefinition.setMaxWaiting(maxWaiting);
		simpleProcessDefinition.setProcessType(processType);

		bind(agentClass).in(Singleton.class);

		getSimpleProcessDefinitionMultibinder().addBinding().toInstance(simpleProcessDefinition);
	}

	protected final <I extends IInjector> void bindInjector(final Class<I> injectorClass) {
		bind(injectorClass).in(Singleton.class);
	}
}
