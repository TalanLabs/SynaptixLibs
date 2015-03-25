package com.synaptix.mybatis.guice;

import java.io.InputStream;
import java.util.Locale;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionManager;

import com.google.inject.Inject;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.synaptix.mybatis.SynaptixMyBatisServer;
import com.synaptix.mybatis.cache.SynaptixCacheManager;
import com.synaptix.mybatis.component.mapper.ComponentMapperManager;
import com.synaptix.mybatis.dao.DaoUserSessionManager;
import com.synaptix.mybatis.dao.IDaoSession;
import com.synaptix.mybatis.dao.IDaoSessionExt;
import com.synaptix.mybatis.dao.IDaoUserContext;
import com.synaptix.mybatis.dao.IGUIDGenerator;
import com.synaptix.mybatis.dao.IReadDaoSession;
import com.synaptix.mybatis.dao.exceptions.VersionConflictDaoException;
import com.synaptix.mybatis.dao.impl.DefaultDaoSession;
import com.synaptix.mybatis.dao.impl.DefaultGUIDGenerator;
import com.synaptix.mybatis.dao.impl.MapperCacheLocal;
import com.synaptix.mybatis.dao.mapper.BusinessMapper;
import com.synaptix.mybatis.dao.mapper.EntitySql;
import com.synaptix.mybatis.dao.mapper.NlsServerMessageMapper;
import com.synaptix.mybatis.dao.mapper.SequenceMapper;
import com.synaptix.mybatis.dao.mapper.TempUserSessionMapper;
import com.synaptix.mybatis.delegate.ComponentServiceDelegate;
import com.synaptix.mybatis.delegate.EntityServiceDelegate;
import com.synaptix.mybatis.delegate.ObjectServiceDelegate;
import com.synaptix.mybatis.filter.AndOperatorProcess;
import com.synaptix.mybatis.filter.AssossiationProcess;
import com.synaptix.mybatis.filter.AssossiationPropertyValueProcess;
import com.synaptix.mybatis.filter.ComparePropertyValueProcess;
import com.synaptix.mybatis.filter.EqualsPropertyValueProcess;
import com.synaptix.mybatis.filter.InPropertyValueProcess;
import com.synaptix.mybatis.filter.LikePropertyValueProcess;
import com.synaptix.mybatis.filter.NodeProcessFactory;
import com.synaptix.mybatis.filter.NotOperatorProcess;
import com.synaptix.mybatis.filter.NullPropertyProcess;
import com.synaptix.mybatis.filter.OrOperatorProcess;
import com.synaptix.mybatis.hack.SynaptixConfiguration;
import com.synaptix.mybatis.hack.SynaptixUserSession;
import com.synaptix.mybatis.helper.ComponentColumnsCache;
import com.synaptix.mybatis.helper.ComponentResultMapHelper;
import com.synaptix.mybatis.helper.ComponentSqlHelper;
import com.synaptix.mybatis.helper.DeleteMappedStatement;
import com.synaptix.mybatis.helper.FindMappedStatement;
import com.synaptix.mybatis.helper.InsertMapMappedStatement;
import com.synaptix.mybatis.helper.InsertMappedStatement;
import com.synaptix.mybatis.helper.PaginationMappedStatement;
import com.synaptix.mybatis.helper.PaginationOldMappedStatement;
import com.synaptix.mybatis.helper.SelectMappedStatement;
import com.synaptix.mybatis.helper.SelectNestedMappedStatement;
import com.synaptix.mybatis.helper.SelectNlsMappedStatement;
import com.synaptix.mybatis.helper.SelectSqlMappedStatement;
import com.synaptix.mybatis.helper.SimplePaginationMappedStatement;
import com.synaptix.mybatis.helper.UpdateMappedStatement;
import com.synaptix.mybatis.service.ComponentServerService;
import com.synaptix.mybatis.service.EntityServerService;
import com.synaptix.mybatis.service.ObjectServerService;
import com.synaptix.mybatis.service.Transactional;
import com.synaptix.server.service.guice.AbstractSynaptixServerServiceModule;
import com.synaptix.service.IComponentService;
import com.synaptix.service.IEntityService;
import com.synaptix.service.ServiceException;
import com.synaptix.service.exceptions.VersionConflictServiceException;

public class SynaptixMyBatisModule extends AbstractSynaptixMyBatisModule {

	private final InputStream configInputStream;

	private final Locale defaultMeaningLocale;

	private final Class<? extends IDaoUserContext> implUserContextClass;

	public SynaptixMyBatisModule(InputStream configInputStream, Class<? extends IDaoUserContext> implUserContextClass) {
		this(configInputStream, Locale.FRENCH, implUserContextClass);
	}

	public SynaptixMyBatisModule(InputStream configInputStream, Locale defaultMeaningLocale, Class<? extends IDaoUserContext> implUserContextClass) {
		super();
		this.configInputStream = configInputStream;
		this.defaultMeaningLocale = defaultMeaningLocale;
		this.implUserContextClass = implUserContextClass;
	}

	@Override
	protected final void configure() {
		bind(InputStream.class).annotatedWith(Names.named("configInputStream")).toInstance(configInputStream);
		bind(Locale.class).annotatedWith(Names.named("defaultMeaningLocale")).toInstance(defaultMeaningLocale);

		bind(SynaptixConfiguration.class).toProvider(SynaptixConfigurationProvider.class).in(Scopes.SINGLETON);
		bind(Configuration.class).to(SynaptixConfiguration.class).in(Scopes.SINGLETON);

		bind(SqlSessionManager.class).toProvider(SynaptixSqlSessionManagerProvider.class).in(Scopes.SINGLETON);
		bind(SqlSession.class).to(SqlSessionManager.class).in(Scopes.SINGLETON);
		bind(SqlSessionFactory.class).toProvider(SynaptixSqlSessionFactoryProvider.class).in(Scopes.SINGLETON);

		addMapperClass(TempUserSessionMapper.class);
		addMapperClass(SequenceMapper.class);
		addMapperClass(BusinessMapper.class);
		addMapperClass(NlsServerMessageMapper.class);

		bind(MapperCacheLocal.class).in(Singleton.class);

		bind(IGUIDGenerator.class).to(DefaultGUIDGenerator.class).in(Singleton.class);
		bind(IDaoUserContext.class).to(implUserContextClass).in(Singleton.class);
		bind(SynaptixUserSession.class).in(Singleton.class);
		bind(DaoUserSessionManager.class).in(Singleton.class);

		bind(DefaultDaoSession.class).in(Singleton.class);
		bind(IReadDaoSession.class).to(DefaultDaoSession.class).in(Singleton.class);
		bind(IDaoSession.class).to(DefaultDaoSession.class).in(Singleton.class);

		bind(SynaptixMyBatisServer.class).in(Singleton.class);

		bind(SynaptixCacheManager.class).in(Singleton.class);
		bind(EntitySql.class).in(Singleton.class);

		bind(ComponentColumnsCache.class).in(Singleton.class);
		bind(ComponentResultMapHelper.class).in(Singleton.class);
		bind(ComponentSqlHelper.class).in(Singleton.class);
		bind(DeleteMappedStatement.class).in(Singleton.class);
		bind(FindMappedStatement.class).in(Singleton.class);
		bind(InsertMappedStatement.class).in(Singleton.class);
		bind(PaginationMappedStatement.class).in(Singleton.class);
		bind(PaginationOldMappedStatement.class).in(Singleton.class);
		bind(SimplePaginationMappedStatement.class).in(Singleton.class);
		bind(SelectMappedStatement.class).in(Singleton.class);
		bind(SelectNestedMappedStatement.class).in(Singleton.class);
		bind(SelectSqlMappedStatement.class).in(Singleton.class);
		bind(UpdateMappedStatement.class).in(Singleton.class);
		bind(SelectNlsMappedStatement.class).in(Singleton.class);
		bind(InsertMapMappedStatement.class).in(Singleton.class);

		bind(OrOperatorProcess.class).in(Singleton.class);
		bind(AndOperatorProcess.class).in(Singleton.class);
		bind(EqualsPropertyValueProcess.class).in(Singleton.class);
		bind(LikePropertyValueProcess.class).in(Singleton.class);
		bind(InPropertyValueProcess.class).in(Singleton.class);
		bind(ComparePropertyValueProcess.class).in(Singleton.class);
		bind(AssossiationPropertyValueProcess.class).in(Singleton.class);
		bind(AssossiationProcess.class).in(Singleton.class);
		bind(NodeProcessFactory.class).in(Singleton.class);
		bind(NullPropertyProcess.class).in(Singleton.class);
		bind(NotOperatorProcess.class).in(Singleton.class);

		bind(ComponentMapperManager.class).in(Singleton.class);

		install(new AbstractSynaptixServerServiceModule() {
			@Override
			protected void configure() {
				bindDelegate(ComponentServiceDelegate.class);
				bindDelegate(EntityServiceDelegate.class);
				bindDelegate(ObjectServiceDelegate.class);

				bindService(ComponentServerService.class).with(IComponentService.class);
				bindService(EntityServerService.class).with(IEntityService.class);
				bindService(ObjectServerService.class);
			}
		});

		TransactionnelMethodInterceptor transactionnelMethodInterceptor = new TransactionnelMethodInterceptor();
		requestInjection(transactionnelMethodInterceptor);

		bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), transactionnelMethodInterceptor);
	}

	private class TransactionnelMethodInterceptor implements MethodInterceptor {

		@Inject
		private IDaoSession daoSession;

		@Inject
		private SqlSessionManager sqlSessionManager;

		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			Transactional transactionnel = invocation.getMethod().getAnnotation(Transactional.class);
			try {
				if (daoSession instanceof IDaoSessionExt) {
					if (!sqlSessionManager.isManagedSessionStarted()) {
						((IDaoSessionExt) daoSession).setCheckVersionConflictDaoExceptionInSession(transactionnel.checkVersionConflict());
					}
				}
				daoSession.begin();
				Object res = invocation.proceed();
				if (transactionnel.commit()) {
					daoSession.commit();
				}
				return res;
			} catch (ServiceException e) {
				throw e;
			} catch (VersionConflictDaoException e) {
				throw new VersionConflictServiceException();
			} catch (Exception e) {
				throw new ServiceException("", e.getMessage(), e);
			} finally {
				daoSession.end();
			}
		}
	}
}
