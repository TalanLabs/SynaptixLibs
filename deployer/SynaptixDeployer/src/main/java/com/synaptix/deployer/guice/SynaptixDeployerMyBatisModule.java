package com.synaptix.deployer.guice;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;

import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.synaptix.deployer.dao.DeployerDaoSession;
import com.synaptix.deployer.dao.IDeployerDaoSession;
import com.synaptix.deployer.mapper.DeployerMapper;
import com.synaptix.mybatis.dao.impl.MapperCacheLocal;
import com.synaptix.mybatis.guice.AbstractSynaptixMyBatisModule;

public class SynaptixDeployerMyBatisModule extends AbstractSynaptixMyBatisModule {

	private String configFile;

	public SynaptixDeployerMyBatisModule(String configFile) {
		super();

		this.configFile = configFile;
	}

	@Override
	protected void configure() {
		bind(String.class).annotatedWith(Names.named("configFile")).toInstance(configFile);

		bind(DeployerDatabaseProvider.class).in(Singleton.class);

		bind(DeployerDaoSession.class).in(Singleton.class);
		bind(IDeployerDaoSession.class).to(DeployerDaoSession.class).in(Singleton.class);

		bind(Configuration.class).toProvider(DeployerConfigurationProvider.class).in(Singleton.class);

		// bind(SqlSession.class).to(SqlSessionManager.class).in(Scopes.SINGLETON);
		bind(SqlSessionFactory.class).toProvider(DeployerDatabaseProvider.class);

		bind(MapperCacheLocal.class);

		addMapperClass(DeployerMapper.class);

		// bind(ComponentColumnsCache.class).in(Singleton.class);
		// bind(ComponentResultMapHelper.class);
	}
}
