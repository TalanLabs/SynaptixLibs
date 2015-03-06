package com.mongo.test.service.dao.access.repository;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.mongo.test.domain.impl.repository.RepositoryImpl;
import com.mongo.test.service.dao.common.AbstractMongoDaoService;
import com.mongo.test.service.dao.common.CommonMongoDaoService;
import com.mongo.test.service.init.DbStarter;

public class RepositoryDaoService extends AbstractMongoDaoService<RepositoryImpl> {

	public interface Factory {
		RepositoryDaoService create(@Assisted String dbName);
	}

	@Inject
	public RepositoryDaoService(DbStarter starter, CommonMongoDaoService cService, @Named("default_db") String default_db) {
		super(RepositoryImpl.class, starter.getDatabaseByName(default_db), cService);
	}

}
