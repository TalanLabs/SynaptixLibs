package com.synaptix.view.swing.error;

import java.awt.Component;

import com.synaptix.swing.utils.DialogErrorMessage;

public class DefaultErrorViewManager extends ErrorViewManager {

	public DefaultErrorViewManager() {
		super();
	}

	@Override
	public void showErrorDialog(Component parent, Throwable t) {
		DialogErrorMessage dialog = new DialogErrorMessage();
		dialog.showDialog(parent, t);
	}
}
