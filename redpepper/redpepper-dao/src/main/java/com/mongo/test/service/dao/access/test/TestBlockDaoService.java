package com.mongo.test.service.dao.access.test;

import javax.annotation.Nullable;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.mongo.test.domain.impl.test.block.TestBlock;
import com.mongo.test.service.dao.common.AbstractMongoDaoService;
import com.mongo.test.service.dao.common.CommonMongoDaoService;
import com.mongo.test.service.init.DbStarter;

public class TestBlockDaoService extends AbstractMongoDaoService<TestBlock> {

	public interface Factory {
		TestBlockDaoService create(@Nullable @Assisted String dbName);
	}

	@Inject
	public TestBlockDaoService(DbStarter starter, CommonMongoDaoService cService, @Nullable @Assisted String dbName, @Named("default_db") String default_db) {
		super(TestBlock.class, starter.getDatabaseByName((dbName == null ? default_db : dbName)), cService);
	}

}
