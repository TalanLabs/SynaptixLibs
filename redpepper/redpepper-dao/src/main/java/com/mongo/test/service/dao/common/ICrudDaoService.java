package com.mongo.test.service.dao.common;

import com.github.jmkgreen.morphia.Key;

public interface ICrudDaoService<T> {

	Key<T> saveAndIndex(T entity);

}
