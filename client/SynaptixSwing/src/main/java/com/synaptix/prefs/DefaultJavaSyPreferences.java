package com.synaptix.prefs;

import java.util.prefs.Preferences;

public class DefaultJavaSyPreferences extends SyPreferences {

	private String save;

	public DefaultJavaSyPreferences() {
		this(""); //$NON-NLS-1$
	}

	public DefaultJavaSyPreferences(String save) {
		this.save = save;
	}

	private String getKey(String key) {
		return save != null ? save + "_" + key : key; //$NON-NLS-1$
	}

	public String get(String key, String def) {
		return Preferences.userRoot().get(getKey(key), def);
	}

	public int getInt(String key, int def) {
		return Preferences.userRoot().getInt(getKey(key), def);
	}

	public boolean getBoolean(String key, boolean def) {
		return Preferences.userRoot().getBoolean(getKey(key), def);
	}

	public void put(String key, String value) {
		Preferences.userRoot().put(getKey(key), value);
	}

	public void putInt(String key, int value) {
		Preferences.userRoot().putInt(getKey(key), value);
	}

	public void putBoolean(String key, boolean value) {
		Preferences.userRoot().putBoolean(getKey(key), value);
	}

	public void remove(String key) {
		Preferences.userRoot().remove(getKey(key));
	}
}
