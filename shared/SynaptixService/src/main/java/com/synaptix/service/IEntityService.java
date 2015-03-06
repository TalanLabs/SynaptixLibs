package com.synaptix.service;

import java.io.Serializable;

import com.synaptix.entity.IEntity;

public interface IEntityService {

	public static final String UNICITY_CONSTRAINT = "unicityConstraint";

	/**
	 * Insert an entity
	 * 
	 * @param entity
	 * @throws ServiceException
	 */
	public <E extends IEntity> Serializable addEntity(E entity) throws ServiceException;

	/**
	 * Update an entity
	 * 
	 * @param entity
	 * @throws ServiceException
	 */
	public <E extends IEntity> Serializable editEntity(E entity) throws ServiceException;

	/**
	 * Remove an entity
	 * 
	 * @param entity
	 * @throws ServiceException
	 */
	public <E extends IEntity> Serializable removeEntity(E entity) throws ServiceException;

	/**
	 * Find a entity by id
	 * 
	 * @param entityClass
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	public <E extends IEntity> E findEntityById(Class<E> entityClass, Serializable id) throws ServiceException;

}
