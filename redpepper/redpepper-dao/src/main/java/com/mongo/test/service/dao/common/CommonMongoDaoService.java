package com.mongo.test.service.dao.common;

import java.util.List;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.query.Query;
import com.google.inject.Inject;
import com.mongo.test.domain.impl.common.TagImpl;
import com.mongo.test.service.dao.access.LuceneDaoService;

public class CommonMongoDaoService {

	private final LuceneDaoService indexationService;

	@Inject
	public CommonMongoDaoService(LuceneDaoService indexationService) {
		this.indexationService = indexationService;
	}

	public <E> List<E> getTaggedItems(com.github.jmkgreen.morphia.Datastore ds, Class<E> resultClass, TagImpl tag) {
		Query<E> q = ds.createQuery(resultClass).field("tags").hasThisOne(tag);
		List<E> items = q.asList();
		return items;
	}

	public void indexEntity(Datastore ds, String collection, String idObject, Object object) {
		indexationService.index(ds, collection, idObject, object);
	}
}
