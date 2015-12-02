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
		registerHandler(Boolean.class, CharToBooleanTypeHandler.class);
		registerHandler(boolean.class, CharToBooleanTypeHandler.class);
		registerHandler(LocalDateTime.class, DateToLocalDateTimeTypeHandler.class);
		registerHandler(LocalDate.class, DateToLocalDateTypeHandler.class);
		registerHandler(LocalTime.class, DateToLocalTimeTypeHandler.class);
		registerHandler(Duration.class, NumberToDurationTypeHandler.class);
		registerHandler(IdRaw.class, RawToIdRawTypeHandler.class);
		registerHandler(IId.class, RawToIIdTypeHandler.class);
		registerHandler(Serializable.class, RawToSerializableTypeHandler.class);
	}

	protected final void registerHandler(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
		configuration.getTypeHandlerRegistry().register(javaTypeClass, typeHandlerClass);
	}

	protected final void registerHandler(Class<?> typeHandlerClass) {
		configuration.getTypeHandlerRegistry().register(typeHandlerClass);
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

	@Inject(optional = true)
	public void setSynaptixUserSession(SynaptixUserSession synaptixUserSession) {
		configuration.setSynaptixUserSession(synaptixUserSession);
	}

	@Override
	public SynaptixConfiguration get() {
		return configuration;
	}
}
