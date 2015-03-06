package com.synaptix.pmgr.guice;

import com.google.inject.Singleton;
import com.synaptix.pmgr.agent.RetryAgent;
import com.synaptix.pmgr.plugin.GuiceIntegratorFactory;
import com.synaptix.pmgr.plugin.GuicePluginManager;
import com.synaptix.pmgr.plugin.IIntegratorFactory;

public class ProcessManagerModule extends AbstractSynaptixIntegratorServletModule {

	@Override
	protected void configure() {
		bind(GuicePluginManager.class).in(Singleton.class);

		bind(GuiceIntegratorFactory.class).in(Singleton.class);
		bind(IIntegratorFactory.class).to(GuiceIntegratorFactory.class).in(Singleton.class);

		bindAgent(RetryAgent.class, 40, 40);

		requestStaticInjection(GuicePluginManager.class);
	}
}
