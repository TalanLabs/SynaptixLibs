package com.mongo.test.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.google.inject.Provider;

public class ConfigProvider implements Provider<Config> {

	private Config config;

	public ConfigProvider() {
		super();

		initConfig();
	}

	private void initConfig() {
		String configFilePath = System.getProperty("test.config");
		Properties p = null;
		if (configFilePath != null) {
			p = new Properties();
			try {
				p.load(new FileReader(configFilePath));
			} catch (IOException e) {
			}
		} else {
			p = new Properties();
			try {
				p.load(ConfigProvider.class.getResourceAsStream("/config.properties"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		config = new Config();
		config.setMongoPort(27017);
		config.setMongoServer("10.61.146.68");
		// config.setMongoPort(Integer.valueOf(p.getProperty("config.mongo.port", "27017")));
		// config.setMongoServer(p.getProperty("config.mongo.host", "10.61.146.68"));
	}

	@Override
	public Config get() {
		return config;
	}
}
