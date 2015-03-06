package com.synaptix.widget.util;

import com.google.inject.Inject;
import com.synaptix.constants.ConstantsBundleManager;
import com.synaptix.widget.guice.SwingConstantsBundleManager;

public abstract class AbstractStaticHelper {

	@Inject
	@SwingConstantsBundleManager
	private static ConstantsBundleManager constantsBundleManager;

	public static ConstantsBundleManager getConstantsBundleManager() {
		return constantsBundleManager;
	}
}
