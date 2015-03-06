package com.synaptix.swing;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.synaptix.local.ILocalMessage;

public class SearchMessages {

	public static final String BUNDLE_NAME = "com.synaptix.swing.searchMessages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private static ILocalMessage localMessage = null;

	private SearchMessages() {
	}

	public static ILocalMessage getLocalMessage() {
		return localMessage;
	}

	public static void setLocalMessage(ILocalMessage localMessage) {
		SearchMessages.localMessage = localMessage;
	}

	public static String getString(String key) {
		String res = null;
		if (localMessage != null) {
			res = localMessage.getMessage(key);
		} else {
			try {
				res = RESOURCE_BUNDLE.getString(key);
			} catch (MissingResourceException e) {
				res = '!' + key + '!';
			}
		}
		return res;
	}
}
