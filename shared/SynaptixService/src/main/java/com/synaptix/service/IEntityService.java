package com.synaptix.service;

import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;

public interface IEntityService {

	public static final String UNICITY_CONSTRAINT = "unicityConstraint";
	public static final String CHECK_CANCEL_CONSTRAINT = "checkCancelConstraint";

	/**
	 * Insert an entity
	 * 
	 * @param entity
	 * @throws ServiceException
	 */
	public <E extends IEntity> IId addEntity(E entity) throws ServiceException;

	/**
	 * Update an entity
	 * 
	 * @param entity
	 * @throws ServiceException
	 */
	public <E extends IEntity> IId editEntity(E entity) throws ServiceException;

	/**
	 * Remove an entity
	 * 
	 * @param entity
	 * @throws ServiceException
	 */
	public <E extends IEntity> IId removeEntity(E entity) throws ServiceException;

	/**
	 * Find a entity by id
	 * 
	 * @param entityClass
	 * @param id
	 * @return
	 * @throws ServiceException
	 */
	public <E extends IEntity> E findEntityById(Class<E> entityClass, IId id) throws ServiceException;

}
