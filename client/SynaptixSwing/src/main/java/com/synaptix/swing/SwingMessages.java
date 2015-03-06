package com.synaptix.swing;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import com.synaptix.local.ILocalMessage;

public class SwingMessages {

	public static final String BUNDLE_NAME = "com.synaptix.swing.swingMessages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private static ILocalMessage localMessage = null;

	private static Properties defaultProperties;

	static {
		defaultProperties = new Properties();
		try {
			defaultProperties
					.load(SwingMessages.class
							.getResourceAsStream("/com/synaptix/swing/swingMessages.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private SwingMessages() {
	}

	public static Properties getDefaultProperties() {
		return defaultProperties;
	}

	public static ILocalMessage getLocalMessage() {
		return localMessage;
	}

	public static void setLocalMessage(ILocalMessage localMessage) {
		SwingMessages.localMessage = localMessage;
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
