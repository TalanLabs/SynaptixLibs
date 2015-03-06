package com.synaptix.widget.guice;

import com.synaptix.guice.module.AbstractSynaptixConstantsBundleModule;
import com.synaptix.widget.constants.SynaptixDateConstantsBundle;
import com.synaptix.widget.constants.SynaptixWidgetConstantsBundle;

public class SynaptixWidgetConstantsBundleModule extends AbstractSynaptixConstantsBundleModule {

	public SynaptixWidgetConstantsBundleModule() {
		super(SwingConstantsBundleManager.class);
	}

	@Override
	protected void configure() {
		bindConstantsBundle(SynaptixWidgetConstantsBundle.class);
		bindConstantsBundle(SynaptixDateConstantsBundle.class);
	}
}
