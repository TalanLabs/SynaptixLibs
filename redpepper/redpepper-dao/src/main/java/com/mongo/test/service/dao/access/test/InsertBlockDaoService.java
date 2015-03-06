package com.mongo.test.service.dao.access.test;

import javax.annotation.Nullable;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.mongo.test.domain.impl.test.block.InsertBlock;
import com.mongo.test.service.dao.common.AbstractMongoDaoService;
import com.mongo.test.service.dao.common.CommonMongoDaoService;
import com.mongo.test.service.init.DbStarter;

public class InsertBlockDaoService extends AbstractMongoDaoService<InsertBlock> {

	public interface Factory {
		InsertBlockDaoService create(@Nullable @Assisted String dbName);
	}

	@Inject
	public InsertBlockDaoService(DbStarter starter, CommonMongoDaoService cService, @Nullable @Assisted String dbName, @Named("default_db") String default_db) {
		super(InsertBlock.class, starter.getDatabaseByName((dbName == null ? default_db : dbName)), cService);
	}

}
