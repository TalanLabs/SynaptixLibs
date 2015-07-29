package com.synaptix.widget.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.synaptix.view.swing.error.ErrorViewManager;
import com.synaptix.widget.SynaptixWidgetClient;
import com.synaptix.widget.core.view.swing.SyDockingContext;
import com.synaptix.widget.error.view.swing.ErrorInfoErrorViewManager;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.GlobalListener;

public class SynaptixWidgetModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new SynaptixWidgetConstantsBundleModule());

		requestStaticInjection(StaticWidgetHelper.class);

		bind(SyDockingContext.class).in(Singleton.class);

		bind(ErrorInfoErrorViewManager.class).in(Singleton.class);
		bind(ErrorViewManager.class).to(ErrorInfoErrorViewManager.class).in(Singleton.class);

		bind(SynaptixWidgetClient.class).in(Singleton.class);

		GlobalListener globalListener = new GlobalListener();
		bind(GlobalListener.class).toInstance(globalListener);
		requestInjection(globalListener);
	}
}
