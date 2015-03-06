package com.synaptix.auth.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.synaptix.auth.AuthsBundle;
import com.synaptix.auth.method.DefaultAuthMethod;
import com.synaptix.auth.method.IAuthMethod;

public class AuthsBundleHelper {

	@SuppressWarnings("unchecked")
	public static final IAuthMethod toAuthMethod(String authMethodString) {
		if (authMethodString == null) {
			return null;
		}
		String[] ss = authMethodString.split("@");
		try {
			Class<?> bundleClass = (Class<?>) AuthsBundleHelper.class
					.getClassLoader().loadClass(ss[0]);
			if (!AuthsBundle.class.isAssignableFrom(bundleClass)) {
				throw new IllegalArgumentException(
						"authMethodString is not correct");
			}
			String[] sss = ss[1].split("&");
			String object = URLDecoder.decode(
					sss[0].substring("object=".length()), "UTF-8");
			String action = URLDecoder.decode(
					sss[1].substring("action=".length()), "UTF-8");
			return new DefaultAuthMethod(
					(Class<? extends AuthsBundle>) bundleClass, object, action);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(
					"authMethodString is not correct");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException(
					"authMethodString is not correct");
		}
	}

}
