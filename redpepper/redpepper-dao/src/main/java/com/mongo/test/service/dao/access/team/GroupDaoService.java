package com.mongo.test.service.dao.access.team;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.mongo.test.domain.impl.team.GroupImpl;
import com.mongo.test.service.dao.common.AbstractMongoDaoService;
import com.mongo.test.service.dao.common.CommonMongoDaoService;
import com.mongo.test.service.init.DbStarter;

public class GroupDaoService extends AbstractMongoDaoService<GroupImpl> {

	public interface Factory {
		GroupDaoService create(@Assisted String dbName);
	}

	@Inject
	public GroupDaoService(DbStarter starter, CommonMongoDaoService cService, @Assisted String dbName) {
		super(GroupImpl.class, starter.getDatabaseByName(dbName), cService);
	}

}
