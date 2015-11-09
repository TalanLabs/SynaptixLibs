package com.synaptix.mybatis.guice;

import java.io.InputStream;
import java.io.Serializable;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.LocalCacheScope;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.synaptix.entity.IId;
import com.synaptix.entity.IdRaw;
import com.synaptix.mybatis.cache.SynaptixCacheManager;
import com.synaptix.mybatis.factory.ComponentObjectFactory;
import com.synaptix.mybatis.hack.SynaptixConfiguration;
import com.synaptix.mybatis.hack.SynaptixUserSession;
import com.synaptix.mybatis.hack.SynaptixXMLConfigBuilder;
import com.synaptix.mybatis.handler.CharToBooleanTypeHandler;
import com.synaptix.mybatis.handler.DateToLocalDateTimeTypeHandler;
import com.synaptix.mybatis.handler.DateToLocalDateTypeHandler;
import com.synaptix.mybatis.handler.DateToLocalTimeTypeHandler;
import com.synaptix.mybatis.handler.NumberToDurationTypeHandler;
import com.synaptix.mybatis.handler.RawToIIdTypeHandler;
import com.synaptix.mybatis.handler.RawToIdRawTypeHandler;
import com.synaptix.mybatis.handler.RawToSerializableTypeHandler;
import com.synaptix.mybatis.helper.ComponentResultMapHelper;
import com.synaptix.mybatis.helper.FindMappedStatement;
import com.synaptix.mybatis.proxy.ComponentProxyFactory;

public class SynaptixConfigurationProvider implements Provider<SynaptixConfiguration> {

	private final SynaptixConfiguration configuration;

	@Inject
	public SynaptixConfigurationProvider(@Named("configInputStream") InputStream configInputStream) {
		super();

		if (configInputStream != null) {
			SynaptixXMLConfigBuilder parser = new SynaptixXMLConfigBuilder(configInputStream, System.getProperty("mybatisEnvironment"), null);
			configuration = parser.parse();
		} else {
			configuration = new SynaptixConfiguration();
		}

		configuration.setObjectFactory(new ComponentObjectFactory());
		configuration.setProxyFactory(new ComponentProxyFactory());
		// configuration.setObjectWrapperFactory(new ComponentObjectWrapperFactory());
		configuration.setLazyLoadingEnabled(true);
		configuration.setAggressiveLazyLoading(false);
		configuration.setCacheEnabled(true);
		configuration.setUseGeneratedKeys(false);
		configuration.setDefaultExecutorType(ExecutorType.SIMPLE);
		configuration.setLocalCacheScope(LocalCacheScope.SESSION);
		registerTypeHandlers();

		configuration.getLanguageRegistry().register(org.mybatis.scripting.velocity.Driver.class);
		configuration.getTypeAliasRegistry().registerAlias("velocity", org.mybatis.scripting.velocity.Driver.class);
	}

	protected void registerTypeHandlers() {
		configuration.getTypeHandlerRegistry().register(Boolean.class, CharToBooleanTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(boolean.class, CharToBooleanTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(LocalDateTime.class, DateToLocalDateTimeTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(LocalDate.class, DateToLocalDateTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(LocalTime.class, DateToLocalTimeTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(Duration.class, NumberToDurationTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(IdRaw.class, RawToIdRawTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(IId.class, RawToIIdTypeHandler.class);
		configuration.getTypeHandlerRegistry().register(Serializable.class, RawToSerializableTypeHandler.class);
	}

	@Inject
	public void setFindMappedStatement(FindMappedStatement findMappedStatement) {
		configuration.setFindMappedStatement(findMappedStatement);
	}

	@Inject
	public void setSynaptixCacheManager(SynaptixCacheManager synaptixCacheManager) {
		configuration.setSynaptixCacheManager(synaptixCacheManager);
	}

	@Inject
	public void setComponentResultMapHelper(ComponentResultMapHelper componentResultMapHelper) {
		configuration.setComponentResultMapHelper(componentResultMapHelper);
	}

	@Inject
	public void setSynaptixUserSession(SynaptixUserSession synaptixUserSession) {
		configuration.setSynaptixUserSession(synaptixUserSession);
	}

	@Override
	public SynaptixConfiguration get() {
		return configuration;
	}
}
