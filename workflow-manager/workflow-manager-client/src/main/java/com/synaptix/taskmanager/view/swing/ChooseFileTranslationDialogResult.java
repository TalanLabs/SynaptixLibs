package com.synaptix.taskmanager.view.swing;

import java.io.File;
import java.util.Locale;

public class ChooseFileTranslationDialogResult {

	private File file;

	private Locale locale;

	public ChooseFileTranslationDialogResult(File file, Locale locale) {
		super();
		this.file = file;
		this.locale = locale;
	}

	public File getFile() {
		return file;
	}

	public Locale getLocale() {
		return locale;
	}
}
