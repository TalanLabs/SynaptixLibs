package com.synaptix.taskmanager.service;

import com.google.inject.Inject;
import com.synaptix.mybatis.dao.IDaoSession;

public abstract class AbstractSimpleService {

	@Inject
	private IDaoSession daoSession;

	public final IDaoSession getDaoSession() {
		return daoSession;
	}
}
