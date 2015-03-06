package com.synaptix.widget.util;

import com.synaptix.widget.constants.SynaptixDateConstantsBundle;
import com.synaptix.widget.constants.SynaptixWidgetConstantsBundle;

public class StaticWidgetHelper extends AbstractStaticHelper {

	public static final SynaptixWidgetConstantsBundle getSynaptixWidgetConstantsBundle() {
		return getConstantsBundleManager().getBundle(SynaptixWidgetConstantsBundle.class);
	}

	public static final SynaptixDateConstantsBundle getSynaptixDateConstantsBundle() {
		return getConstantsBundleManager().getBundle(SynaptixDateConstantsBundle.class);
	}
}
