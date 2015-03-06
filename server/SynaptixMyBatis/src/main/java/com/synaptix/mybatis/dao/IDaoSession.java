package com.synaptix.mybatis.dao;

import org.apache.ibatis.session.SqlSession;

import com.synaptix.entity.ICancellable;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
import com.synaptix.mybatis.dao.listener.IEntitySaveOrUpdateListener;
import com.synaptix.mybatis.dao.mapper.EntitySql;

public interface IDaoSession extends IReadDaoSession {

	/**
	 * Commit sql session
	 */
	public void commit();

	/**
	 * Get sql session from myBatis
	 * 
	 * @return
	 */
	public SqlSession getSqlSession();

	/**
	 * Save a entity object, create a new id and version is 0
	 * 
	 * @param entity
	 * @return
	 */
	public <T extends IEntity> int saveEntity(T entity);

	/**
	 * Save a entity object, if id exist then update and increment version else create a new id and version is 0
	 * 
	 * @param entity
	 * @return
	 */
	public <T extends IEntity> int saveOrUpdateEntity(T entity);

	/**
	 * Delete a entity object
	 * 
	 * @param entity
	 * @return
	 */
	public <T extends IEntity> int deleteEntity(T entity);

	/**
	 * Cancel a entity object
	 * 
	 * @param entity
	 * @return
	 */
	public <T extends IEntity & ICancellable> int cancelEntity(T entity);

	/**
	 * Get a new ID
	 * 
	 * @return
	 */
	public IId newId();

	/**
	 * Get a entity sql, insert, update and delete method
	 */
	public EntitySql getEntitySql();

	/**
	 * Adds an entity save or update listener which will be called before any save or update
	 * 
	 * @param entityClass
	 * @param entitySaveOrUpdateListener
	 */
	public <T extends IEntity> void addEntitySaveOrUpdateListener(Class<T> entityClass, IEntitySaveOrUpdateListener<T> entitySaveOrUpdateListener);

}
