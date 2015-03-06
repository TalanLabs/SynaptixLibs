package com.synaptix.deployer.guice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.deployer.helper.DatabaseHelper;
import com.synaptix.deployer.model.IConfig;

public class DeployerDatabaseProvider implements Provider<SqlSessionFactory> {

	private static final Log LOG = LogFactory.getLog(DeployerDatabaseProvider.class);

	private Injector injector;

	private static IConfig config;

	@Inject
	public DeployerDatabaseProvider(Injector injector) {
		super();

		this.injector = injector;

		DatabaseHelper.getInstance().setInjector(injector);
	}

	//
	// public void setEnvironmentId(String environmentId2) {
	// environmentId = environmentId2;
	// }

	@Override
	public SqlSessionFactory get() {
		if (config == null) {
			SqlSessionFactory sqlSessionFactory = createEnvironmentId(null);
			return sqlSessionFactory;
		} else {
			return DatabaseHelper.getInstance().getSqlSessionFactory(config, this);
		}
	}

	public SqlSessionFactory createEnvironmentId(IConfig config2) {
		config = config2;
		Configuration configuration = DatabaseHelper.getInstance().getConfig(config);
		if (configuration == null) {
			DeployerConfigurationProvider configProvider = injector.getProvider(DeployerConfigurationProvider.class).get();
			if (config != null) {
				configProvider.setConfig(config);
			} else {
				config = ComponentFactory.getInstance().createInstance(IConfig.class);
			}
			configuration = configProvider.get();
			config.setEnvironment(configuration.getEnvironment().getId());

			Map<Integer, Class<?>> mapperMap = injector.getInstance(Key.get(new TypeLiteral<Map<Integer, Class<?>>>() {
			}));
			if (mapperMap != null && !mapperMap.isEmpty()) {
				List<Integer> ls = new ArrayList<Integer>(mapperMap.keySet());
				Collections.sort(ls);
				for (Integer l : ls) {
					Class<?> mapper = mapperMap.get(l);
					LOG.info("Add mapper for " + configuration.getEnvironment().getId() + " " + config.getUser() + " " + mapper);
					configuration.addMapper(mapper);
				}
			}
		}

		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		DatabaseHelper.getInstance().addSqlSessionFactory(config, sqlSessionFactory);
		return sqlSessionFactory;
	}
}
