package com.synaptix.deployer.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.ibatis.session.SqlSessionFactory;

import com.google.inject.Injector;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.deployer.guice.DeployerDatabaseProvider;
import com.synaptix.deployer.model.IConfig;
import com.synaptix.mybatis.hack.SynaptixConfiguration;

public class DatabaseHelper {

	private static DatabaseHelper instance;

	private final Map<IConfig, SqlSessionFactory> sqlSessionFactoryMap;

	private final Map<IConfig, SynaptixConfiguration> configMap;

	private Injector injector;

	private DatabaseHelper() {
		super();

		sqlSessionFactoryMap = new HashMap<IConfig, SqlSessionFactory>();
		configMap = new HashMap<IConfig, SynaptixConfiguration>();
	}

	public void setInjector(Injector injector) {
		this.injector = injector;
	}

	public static DatabaseHelper getInstance() {
		if (instance == null) {
			instance = new DatabaseHelper();
		}
		return instance;
	}

	public SqlSessionFactory getSqlSessionFactory(IConfig config, DeployerDatabaseProvider deployerDatabaseProvider) {
		SqlSessionFactory sqlSessionFactory = sqlSessionFactoryMap.get(config);
		if (sqlSessionFactory == null) {
			if (deployerDatabaseProvider == null) {
				deployerDatabaseProvider = injector.getInstance(DeployerDatabaseProvider.class);
			}
			sqlSessionFactory = deployerDatabaseProvider.createEnvironmentId(config);
		}
		return sqlSessionFactory;
	}

	public void addConfig(IConfig config, SynaptixConfiguration synaptixConfiguration) {
		configMap.put(config, synaptixConfiguration);
	}

	public SynaptixConfiguration getConfig(IConfig config) {
		for (Entry<IConfig, SynaptixConfiguration> entry : configMap.entrySet()) {
			if (isEquals(entry.getKey(), config)) {
				return entry.getValue();
			}
		}
		return null;
	}

	private boolean isEquals(IConfig key, IConfig config) {
		Set<String> equalsKey = ComponentFactory.getInstance().getDescriptor(IConfig.class).getEqualsKeyPropertyNames();
		for (String equals : equalsKey) {
			Object v1 = key.straightGetProperty(equals);
			Object v2 = config.straightGetProperty(equals);
			if (v1 != null ? !v1.equals(v2) : v2 != null) {
				return false;
			}
		}
		return true;
	}

	public void addSqlSessionFactory(IConfig config, SqlSessionFactory sqlSessionFactory) {
		// if (config == null) {
		// config = sqlSessionFactory.getConfiguration().getEnvironment().getId();
		// }
		sqlSessionFactoryMap.put(config, sqlSessionFactory);
	}
}
