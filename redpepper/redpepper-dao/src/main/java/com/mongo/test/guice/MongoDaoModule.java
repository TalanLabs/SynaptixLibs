package com.mongo.test.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.mongo.test.service.dao.access.LuceneDaoService;
import com.mongo.test.service.dao.access.project.CampaignDaoService;
import com.mongo.test.service.dao.access.project.ProjectDaoService;
import com.mongo.test.service.dao.access.repository.RepositoryDaoService;
import com.mongo.test.service.dao.access.repository.WebElementDaoService;
import com.mongo.test.service.dao.access.team.GroupDaoService;
import com.mongo.test.service.dao.access.team.UserDaoService;
import com.mongo.test.service.dao.access.test.CommentBlockDaoService;
import com.mongo.test.service.dao.access.test.ConfigBlockDaoService;
import com.mongo.test.service.dao.access.test.InsertBlockDaoService;
import com.mongo.test.service.dao.access.test.SetupBlockDaoService;
import com.mongo.test.service.dao.access.test.TestBlockDaoService;
import com.mongo.test.service.dao.access.test.TestPageDaoService;
import com.mongo.test.service.dao.common.CommonMongoDaoService;

public class MongoDaoModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(LuceneDaoService.class).in(Singleton.class);
		bind(CommonMongoDaoService.class).in(Singleton.class);

		install(new FactoryModuleBuilder().build(RepositoryDaoService.Factory.class));
		install(new FactoryModuleBuilder().build(WebElementDaoService.Factory.class));

		install(new FactoryModuleBuilder().build(TestPageDaoService.Factory.class));
		install(new FactoryModuleBuilder().build(CampaignDaoService.Factory.class));
		install(new FactoryModuleBuilder().build(ProjectDaoService.Factory.class));

		install(new FactoryModuleBuilder().build(GroupDaoService.Factory.class));
		install(new FactoryModuleBuilder().build(UserDaoService.Factory.class));

		install(new FactoryModuleBuilder().build(CommentBlockDaoService.Factory.class));
		install(new FactoryModuleBuilder().build(ConfigBlockDaoService.Factory.class));
		install(new FactoryModuleBuilder().build(InsertBlockDaoService.Factory.class));
		install(new FactoryModuleBuilder().build(SetupBlockDaoService.Factory.class));
		install(new FactoryModuleBuilder().build(TestBlockDaoService.Factory.class));
	}

}
