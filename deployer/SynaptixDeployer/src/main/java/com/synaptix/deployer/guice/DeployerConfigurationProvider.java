package com.synaptix.deployer.guice;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.deployer.helper.DatabaseHelper;
import com.synaptix.deployer.model.IConfig;
import com.synaptix.entity.IId;
import com.synaptix.entity.IdRaw;
import com.synaptix.mybatis.factory.ComponentObjectFactory;
import com.synaptix.mybatis.hack.SynaptixConfiguration;
import com.synaptix.mybatis.hack.SynaptixXMLConfigBuilder;
import com.synaptix.mybatis.handler.CharToBooleanTypeHandler;
import com.synaptix.mybatis.handler.DateToLocalDateTimeTypeHandler;
import com.synaptix.mybatis.handler.DateToLocalDateTypeHandler;
import com.synaptix.mybatis.handler.DateToLocalTimeTypeHandler;
import com.synaptix.mybatis.handler.NumberToDurationTypeHandler;
import com.synaptix.mybatis.handler.RawToIIdTypeHandler;
import com.synaptix.mybatis.handler.RawToIdRawTypeHandler;
import com.synaptix.mybatis.helper.ComponentColumnsCache;
import com.synaptix.mybatis.helper.ComponentResultMapHelper;
import com.synaptix.mybatis.proxy.ComponentProxyFactory;

public class DeployerConfigurationProvider implements Provider<Configuration> {

	private static final Log LOG = LogFactory.getLog(DeployerConfigurationProvider.class);

	private String configFile;

	private static IConfig config;

	@Inject
	private Injector injector;

	@Inject
	public DeployerConfigurationProvider(@Named("configFile") String configFile) {
		super();

		this.configFile = configFile;
	}

	public void setConfig(IConfig config2) {
		config = config2;
	}

	@Override
	public Configuration get() {
		SynaptixConfiguration synaptixConfiguration = null;
		InputStream configInputStream = DeployerConfigurationProvider.class.getResourceAsStream(configFile);
		if (config == null) {
			synaptixConfiguration = new SynaptixXMLConfigBuilder(configInputStream).parse();
			initConfig(synaptixConfiguration);
			config = ComponentFactory.getInstance().createInstance(IConfig.class);
			// config.setSynaptixConfiguration(synaptixConfiguration);
			config.setEnvironment(synaptixConfiguration.getEnvironment().getId());
			DatabaseHelper.getInstance().addConfig(config, synaptixConfiguration);
		} else {
			synaptixConfiguration = DatabaseHelper.getInstance().getConfig(config);
			if (synaptixConfiguration == null) {
				if (config.getUser() != null) {
					Properties props = new Properties();
					props.put("username", config.getUser());
					props.put("password", new String(config.getPassword()));
					synaptixConfiguration = new SynaptixXMLConfigBuilder(configInputStream, config.getEnvironment(), props).parse();
				} else {
					synaptixConfiguration = new SynaptixXMLConfigBuilder(configInputStream, config.getEnvironment()).parse();
				}

				// <properties resource="org/mybatis/example/config.properties">
				// <property name="username" value="dev_user"/>
				// <property name="password" value="F2Fa3!33TYyg"/>
				// </properties>

				initConfig(synaptixConfiguration);
				DatabaseHelper.getInstance().addConfig(config, synaptixConfiguration);
			}
		}
		return synaptixConfiguration;
	}

	private void initConfig(SynaptixConfiguration configuration) {
//		configuration.setFindMappedStatement(injector.getInstance(FindMappedStatement.class)); // TODO repare... circular dependency
		ComponentColumnsCache componentColumnsCache = new ComponentColumnsCache();
		ComponentResultMapHelper componentResultMapHelper = new ComponentResultMapHelper();
		configuration.setComponentResultMapHelper(componentResultMapHelper);
		componentResultMapHelper.setSynaptixConfiguration(configuration);
		componentResultMapHelper.setComponentColumnsCache(componentColumnsCache);

		configuration.setObjectFactory(new ComponentObjectFactory());
		configuration.setProxyFactory(new ComponentProxyFactory());
		configuration.setLazyLoadingEnabled(true);
		configuration.setAggressiveLazyLoading(false);
		configuration.setCacheEnabled(true);
		configuration.setUseGeneratedKeys(false);
		configuration.setDefaultExecutorType(ExecutorType.SIMPLE);
		configuration.setLocalCacheScope(LocalCacheScope.SESSION);
		configuration.getTypeHandlerRegistry().register(Boolean.class, CharToBooleanTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(boolean.class, CharToBooleanTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(LocalDateTime.class, DateToLocalDateTimeTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(LocalDate.class, DateToLocalDateTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(LocalTime.class, DateToLocalTimeTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(Duration.class, NumberToDurationTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(IdRaw.class, RawToIdRawTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(IId.class, RawToIIdTypeHandler.class);
//		configuration.getTypeHandlerRegistry().register(Serializable.class, RawToSerializableTypeHandler.class);
	}
}
