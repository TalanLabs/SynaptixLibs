package com.synaptix.deployer.client.guice;

import com.google.inject.AbstractModule;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.synaptix.constants.ConstantsBundleManager;
import com.synaptix.constants.DefaultConstantsBundleManager;
import com.synaptix.deployer.client.controller.IDeployerContext;
import com.synaptix.deployer.client.controller.SynaptixDeployerController;
import com.synaptix.deployer.client.core.SynaptixDeployerContext;
import com.synaptix.deployer.client.util.DeployerManager;
import com.synaptix.deployer.client.util.IDeployerManager;
import com.synaptix.deployer.client.util.StaticHelper;
import com.synaptix.deployer.client.view.ISynaptixDeployerViewFactory;
import com.synaptix.deployer.client.view.ISynaptixDeployerWindow;
import com.synaptix.deployer.client.view.swing.SwingSynaptixDeployerViewFactory;
import com.synaptix.deployer.client.view.swing.SynaptixDeployerFrame;
import com.synaptix.prefs.FakeSyPreferences;
import com.synaptix.prefs.SyPreferences;
import com.synaptix.widget.guice.SwingConstantsBundleManager;
import com.synaptix.widget.guice.SynaptixWidgetModule;

public class SynaptixClientDeployerModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new SynaptixWidgetModule());
		install(new SynaptixDeployerConstantsBundleClientModule());

		requestStaticInjection(StaticHelper.class);

		initPreferences();

		bind(DeployerManager.class).in(Singleton.class);
		bind(IDeployerManager.class).to(DeployerManager.class).in(Singleton.class);

		bind(ConstantsBundleManager.class).annotatedWith(SwingConstantsBundleManager.class).to(DefaultConstantsBundleManager.class).in(Singleton.class);

		bind(ISynaptixDeployerViewFactory.class).to(SwingSynaptixDeployerViewFactory.class).in(Singleton.class);

		install(new FrontendFrameModule());
		bind(SynaptixDeployerController.class);
	}

	private void initPreferences() {
		SyPreferences.setPreferences(new FakeSyPreferences());
	}

	private final class FrontendFrameModule extends PrivateModule {

		@Override
		protected void configure() {
			bind(IDeployerContext.class).to(SynaptixDeployerContext.class).in(Singleton.class);
			bind(SynaptixDeployerContext.class).in(Singleton.class);
			expose(SynaptixDeployerContext.class);

			bind(ISynaptixDeployerWindow.class).to(SynaptixDeployerFrame.class).in(Singleton.class);
		}
	}
}
