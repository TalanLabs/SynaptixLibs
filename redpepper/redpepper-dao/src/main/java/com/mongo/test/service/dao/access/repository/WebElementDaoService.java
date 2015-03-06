package com.mongo.test.service.dao.access.repository;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.mongo.test.domain.impl.repository.WebElementImpl;
import com.mongo.test.service.dao.common.AbstractMongoDaoService;
import com.mongo.test.service.dao.common.CommonMongoDaoService;
import com.mongo.test.service.init.DbStarter;

public class WebElementDaoService extends AbstractMongoDaoService<WebElementImpl> {

	public interface Factory {
		WebElementDaoService create(@Assisted String dbName);
	}

	@Inject
	public WebElementDaoService(DbStarter starter, CommonMongoDaoService cService, @Assisted String dbName, @Named("default_db") String default_db) {
		super(WebElementImpl.class, starter.getDatabaseByName((dbName == null ? default_db : dbName)), cService);
	}

}
