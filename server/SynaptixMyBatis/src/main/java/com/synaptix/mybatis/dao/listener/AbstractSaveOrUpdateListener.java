package com.synaptix.mybatis.dao.listener;

import com.synaptix.entity.IEntity;

public abstract class AbstractSaveOrUpdateListener<T extends IEntity> implements IEntitySaveOrUpdateListener<T> {

	@Override
	public void beforeSave(T entity) {
	}

	@Override
	public void beforeUpdate(T entity) {
	}

	@Override
	public void afterSave(T entity) {
	}

	@Override
	public void afterUpdate(T entity) {
	}
}
