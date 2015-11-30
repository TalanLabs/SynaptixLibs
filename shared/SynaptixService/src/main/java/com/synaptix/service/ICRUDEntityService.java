package com.synaptix.service;

import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;

/**
 * @param <E>
 */
public interface ICRUDEntityService<E extends IEntity> {

	public static final String UNICITY_CONSTRAINT = "unicityConstraint";

	/**
	 * Insert an entity
	 * 
	 * @param entity
	 * @throws ServiceException
	 */
	public IId addCRUDEntity(E entity) throws ServiceException;

	/**
	 * Update an entity
	 * 
	 * @param entity
	 * @throws ServiceException
	 */
	public IId editCRUDEntity(E entity) throws ServiceException;

	/**
	 * Remove an entity
	 * 
	 * @param entity
	 * @throws ServiceException
	 */
	public IId removeCRUDEntity(E entity) throws ServiceException;

}
