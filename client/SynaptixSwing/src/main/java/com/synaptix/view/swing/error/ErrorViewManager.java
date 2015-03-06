package com.synaptix.view.swing.error;

import java.awt.Component;

public abstract class ErrorViewManager {

	private static ErrorViewManager instance;

	public static final ErrorViewManager getInstance() {
		if (instance == null) {
			instance = new DefaultErrorViewManager();
		}
		return instance;
	}

	public static void setInstance(ErrorViewManager instance) {
		ErrorViewManager.instance = instance;
	}

	public abstract void showErrorDialog(Component parent, Throwable t);
}
