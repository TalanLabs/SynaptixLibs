package com.mongo.test.service.dao.access.team;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.mongo.test.domain.impl.team.UserImpl;
import com.mongo.test.service.dao.common.AbstractMongoDaoService;
import com.mongo.test.service.dao.common.CommonMongoDaoService;
import com.mongo.test.service.init.DbStarter;

public class UserDaoService extends AbstractMongoDaoService<UserImpl> {

	public interface Factory {
		UserDaoService create(@Assisted String dbName);
	}

	@Inject
	public UserDaoService(DbStarter starter, CommonMongoDaoService cService, @Assisted String dbName) {
		super(UserImpl.class, starter.getDatabaseByName(dbName), cService);
	}

}
