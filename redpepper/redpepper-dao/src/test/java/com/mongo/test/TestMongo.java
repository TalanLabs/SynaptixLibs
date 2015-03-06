package com.mongo.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongo.test.domain.impl.repository.RepositoryImpl;
import com.mongo.test.guice.MongoModule;
import com.mongo.test.service.dao.access.repository.RepositoryDaoService;

public class TestMongo {

	public static void main(String[] args) {
		Injector in = Guice.createInjector(new MongoModule());

		RepositoryDaoService.Factory repoFactory = in.getInstance(RepositoryDaoService.Factory.class);
		RepositoryDaoService service = repoFactory.create(null);
		RepositoryImpl rpo = new RepositoryImpl();
		rpo.setName("new test for repo");
		service.saveAndIndex(rpo);

	}

}
