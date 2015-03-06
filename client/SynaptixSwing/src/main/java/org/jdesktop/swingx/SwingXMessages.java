package org.jdesktop.swingx;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SwingXMessages {
	private static final String BUNDLE_NAME = "org.jdesktop.swingx.swingXMessages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private SwingXMessages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
