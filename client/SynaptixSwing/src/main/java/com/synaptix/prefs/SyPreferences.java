package com.synaptix.prefs;

public abstract class SyPreferences {

	private static SyPreferences prefs;

	static {
		prefs = new DefaultJavaSyPreferences();
	}

	public static SyPreferences getPreferences() {
		return prefs;
	}

	public static void setPreferences(SyPreferences preferences) {
		prefs = preferences;
	}

	public abstract void put(String key, String value);

	public abstract void putInt(String key, int value);
	
	public abstract void putBoolean(String key, boolean value);
	
	public abstract String get(String key, String def);

	public abstract int getInt(String key, int def);

	public abstract boolean getBoolean(String key, boolean def);

	public abstract void remove(String key);

}
