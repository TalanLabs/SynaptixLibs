package com.synaptix.widget.helper;

import com.synaptix.prefs.SyPreferences;

public final class FileHelper {

	private FileHelper() {
	}

	public static final String verifyExtension(String filename, String extension) {
		return filename.toLowerCase().endsWith(extension) ? filename : filename
				+ extension;
	}

	public static final void saveLastDirectorySave(String path) {
		if (path != null) {
			SyPreferences.getPreferences().put("lastDirectorySave", path);
		}
	}

	public static final String loadLastDirectorySave() {
		return SyPreferences.getPreferences().get("lastDirectorySave", null);
	}

	public static final void saveLastDirectoryOpen(String path) {
		if (path != null) {
			SyPreferences.getPreferences().put("lastDirectoryOpen", path);
		}
	}

	public static final String loadLastDirectoryOpen() {
		return SyPreferences.getPreferences().get("lastDirectoryOpen", null);
	}
}
