package com.mongo.test.service.dao.access.test;

import javax.annotation.Nullable;

import com.github.jmkgreen.morphia.query.Query;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.mongo.test.domain.impl.test.TestPage;
import com.mongo.test.service.dao.common.AbstractMongoDaoService;
import com.mongo.test.service.dao.common.CommonMongoDaoService;
import com.mongo.test.service.init.DbStarter;

public class TestPageDaoService extends AbstractMongoDaoService<TestPage> {

	public interface Factory {
		TestPageDaoService create(@Nullable @Assisted String dbName);
	}

	@Inject
	public TestPageDaoService(DbStarter starter, CommonMongoDaoService cService, @Nullable @Assisted String dbName, @Named("default_db") String default_db) {
		super(TestPage.class, starter.getDatabaseByName((dbName == null ? default_db : dbName)), cService);
	}

	public TestPage getByName(String name) {
		Query<TestPage> query = createQuery();
		query.field("pageName").equals(name);
		return query.get();
	}

	public TestPage saveAsNewIteration(TestPage c) {
		c.setId(null);
		// List<IBlock> blocks = c.getBlocks();
		// if (blocks != null && !blocks.isEmpty()) {
		// for (IBlock b : blocks) {
		// b.setId(null);
		// testServiceFactory.saveEntity(b);
		// }
		// }
		save(c);
		return c;
	}
}
