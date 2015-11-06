package com.synaptix.taskmanager.service;

import org.apache.ibatis.session.SqlSession;

import com.google.inject.Inject;
import com.synaptix.entity.ICancellable;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
import com.synaptix.mybatis.dao.IDaoSession;
import com.synaptix.mybatis.dao.mapper.EntitySql;

public abstract class AbstractDelegate {

	private IDaoSession daoSession;

	@Inject
	public void setDaoSession(IDaoSession daoSession) { // used by some tests... to be improved!
		this.daoSession = daoSession;
	}

	protected SqlSession getSqlSession() {
		return daoSession.getSqlSession();
	}

	protected <M> M getMapper(Class<M> mapperClass) {
		return daoSession.getMapper(mapperClass);
	}

	/**
	 * Save a entity object, create a new id and version is 0
	 *
	 * @param entity
	 * @return
	 */
	protected <T extends IEntity> int saveEntity(T entity) {
		return daoSession.saveEntity(entity);
	}

	/**
	 * Save a entity object, if id exist then update and increment version else create a new id and version is 0
	 *
	 * @param entity
	 * @return
	 */
	protected <T extends IEntity> int saveOrUpdateEntity(T entity) {
		return daoSession.saveOrUpdateEntity(entity);
	}

	/**
	 * Delete a entity object
	 *
	 * @param entity
	 * @return
	 */
	protected <T extends IEntity> int deleteEntity(T entity) {
		return daoSession.deleteEntity(entity);
	}

	/**
	 * Cancel a entity object
	 *
	 * @param entity
	 * @return
	 */
	protected <T extends IEntity & ICancellable> int cancelEntity(T entity) {
		return daoSession.cancelEntity(entity);
	}

	/**
	 * Get a new ID
	 *
	 * @return
	 */
	protected final IId getNewId() {
		return daoSession.newId();
	}

	/**
	 * Get a entity sql, insert, update and delete method
	 */
	protected EntitySql getEntitySql() {
		return daoSession.getEntitySql();
	}
}
