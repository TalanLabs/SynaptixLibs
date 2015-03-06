package com.synaptix.prefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FakeSyPreferences extends SyPreferences {

	private static final Log LOG = LogFactory.getLog(FakeSyPreferences.class);

	private File file;

	private Properties properties;

	public FakeSyPreferences() {
		properties = new Properties();

		File parentFile = null;
		try {
			parentFile = new File(System.getProperty("java.io.tmpdir"));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		if (parentFile != null && parentFile.exists()) {
			file = new File(parentFile, "test.properties");
		} else {
			file = new File("test.properties");
		}
		LOG.info("Fake properties file : " + file.getAbsolutePath());
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		load();
	}

	private void load() {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			properties.load(fileInputStream);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				fileInputStream.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	private void save() {
		if (file.lastModified() - System.currentTimeMillis() > 1 * 60 * 1000) {
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(file);
				properties.store(fileOutputStream, null);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			} finally {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}

	public String get(String key, String def) {
		return properties.getProperty(key, def);
	}

	public boolean getBoolean(String key, boolean def) {
		return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(def)));
	}

	public int getInt(String key, int def) {
		return Integer.parseInt(properties.getProperty(key, String.valueOf(def)));
	}

	public void put(String key, String value) {
		properties.put(key, value);

		save();
	}

	public void putBoolean(String key, boolean value) {
		put(key, String.valueOf(value));
	}

	public void putInt(String key, int value) {
		put(key, String.valueOf(value));
	}

	public void remove(String key) {
		properties.remove(key);

		save();
	}
}
