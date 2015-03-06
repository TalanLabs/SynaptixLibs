package com.mongo.test.guice;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.mongo.test.config.Config;
import com.mongo.test.config.ConfigProvider;
import com.mongo.test.service.dao.common.EntityCollectionManager;
import com.mongo.test.service.init.DbStarter;
import com.mongo.test.service.init.MongoDefaultStarterImpl;

public class MongoModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(DbStarter.class).to(MongoDefaultStarterImpl.class).asEagerSingleton();
		bind(Config.class).toProvider(ConfigProvider.class).in(Singleton.class);
		bind(EntityCollectionManager.class).in(Singleton.class);
		bindConstant().annotatedWith(Names.named("default_db")).to("test_project_db");

		/* analyzer stuff - adding lucene capabilities search */
		bind(Analyzer.class).toInstance(new StandardAnalyzer(Version.LUCENE_30));

		install(new MongoDaoModule());
	}
}
