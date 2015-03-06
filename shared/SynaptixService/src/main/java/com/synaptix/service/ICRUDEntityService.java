package com.synaptix.service;

import java.io.Serializable;

import com.synaptix.entity.IEntity;

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
	public Serializable addCRUDEntity(E entity) throws ServiceException;

	/**
	 * Update an entity
	 * 
	 * @param entity
	 * @throws ServiceException
	 */
	public Serializable editCRUDEntity(E entity) throws ServiceException;

	/**
	 * Remove an entity
	 * 
	 * @param entity
	 * @throws ServiceException
	 */
	public Serializable removeCRUDEntity(E entity) throws ServiceException;

}
