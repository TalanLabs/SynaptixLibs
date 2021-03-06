package com.mongo.test.service.dao.access.project;

import com.github.jmkgreen.morphia.query.Query;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.mongo.test.domain.impl.report.Campaign;
import com.mongo.test.domain.impl.test.TestPage;
import com.mongo.test.service.dao.access.test.TestPageDaoService;
import com.mongo.test.service.dao.common.AbstractMongoDaoService;
import com.mongo.test.service.dao.common.CommonMongoDaoService;
import com.mongo.test.service.init.DbStarter;

public class CampaignDaoService extends AbstractMongoDaoService<Campaign> {

	public interface Factory {
		CampaignDaoService create(@Assisted String dbName);
	}

	TestPageDaoService tService;

	@Inject
	public CampaignDaoService(DbStarter starter, CommonMongoDaoService cService, @Assisted String dbName, @Named("default_db") String default_db, TestPageDaoService.Factory tDaoServiceFactory) {
		super(Campaign.class, starter.getDatabaseByName((dbName == null ? default_db : dbName)), cService);
		tService = tDaoServiceFactory.create(dbName);
	}

	public Campaign getByName(String name) {
		Query<Campaign> query = createQuery();
		query.field("name").equal(name).order("-iteration");
		return find(query).get();
	}

	public Campaign saveAsNewIteration(Campaign c) {
		c.setId(null);
		for (TestPage t : c.getTestCases()) {
			tService.saveAsNewIteration(t);
		}
		save(c);
		return c;
	}

}
