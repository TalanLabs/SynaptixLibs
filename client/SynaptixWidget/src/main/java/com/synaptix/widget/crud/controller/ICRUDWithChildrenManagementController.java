package com.synaptix.widget.crud.controller;

import com.synaptix.entity.IEntity;

/**
 * Interface for CRUD controllers
 * 
 * @author Nicolas P
 * 
 * @param <E>
 */
public interface ICRUDWithChildrenManagementController<E extends IEntity, C extends IEntity> extends ICRUDManagementController<E> {

	/**
	 * Add child entity
	 */
	public void addChildEntity(E paginationEntity);

	/**
	 * Edit a child entity
	 * 
	 * @param paginationEntity
	 * @param childEntity
	 */
	public void editChildEntity(E paginationEntity, C childEntity);

	/**
	 * Show a child entity
	 * 
	 * @param paginationEntity
	 * @param childEntity
	 */
	public void showChildEntity(E paginationEntity, C childEntity);

	/**
	 * Clone a child entity
	 * 
	 * @param paginationEntity
	 * @param childEntity
	 */
	public void cloneChildEntity(E paginationEntity, C childEntity);

	/**
	 * Delete a child entity
	 * 
	 * @param paginationEntity
	 * @param childEntity
	 */
	public void deleteChildEntity(E paginationEntity, C childEntity);

	/**
	 * Has authorisation to write children
	 * 
	 * @return
	 */
	public boolean hasAuthWriteChildren();

	/**
	 * Load children
	 * 
	 * @param paginationEntity
	 */
	public void loadChildren(E paginationEntity);

}
