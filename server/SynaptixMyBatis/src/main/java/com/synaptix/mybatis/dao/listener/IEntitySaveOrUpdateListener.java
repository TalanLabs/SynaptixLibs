package com.synaptix.mybatis.dao.listener;

import com.synaptix.entity.IEntity;

public interface IEntitySaveOrUpdateListener<T extends IEntity> {

	public void beforeSave(T entity);

	public void beforeUpdate(T entity);

	public void afterSave(T entity);

	public void afterUpdate(T entity);

}
