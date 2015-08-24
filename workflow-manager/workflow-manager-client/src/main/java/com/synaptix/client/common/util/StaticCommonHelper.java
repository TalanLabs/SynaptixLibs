package com.synaptix.client.common.util;

import com.synaptix.auth.AuthsBundleManager;
import com.synaptix.client.common.message.CommonAuthsBundle;
import com.synaptix.client.common.message.CommonConstantsBundle;
import com.synaptix.client.common.message.DateConstantsBundle;
import com.synaptix.client.common.message.NlsMessageDataTableConstantsBundle;
import com.synaptix.widget.util.AbstractStaticHelper;

public class StaticCommonHelper extends AbstractStaticHelper {

	public static CommonConstantsBundle getCommonConstantsBundle() {
		return getConstantsBundleManager().getBundle(CommonConstantsBundle.class);
	}

	public static NlsMessageDataTableConstantsBundle getNlsMessageDataTableConstantsBundle() {
		return getConstantsBundleManager().getBundle(NlsMessageDataTableConstantsBundle.class);
	}

	public static CommonAuthsBundle getCommonAuthsBundle() {
		return AuthsBundleManager.getInstance().getBundle(CommonAuthsBundle.class);
	}

	public static DateConstantsBundle getDateConstantsBundle() {
		return getConstantsBundleManager().getBundle(DateConstantsBundle.class);
	}

}
