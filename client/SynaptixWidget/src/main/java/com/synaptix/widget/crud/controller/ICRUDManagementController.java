package com.synaptix.widget.crud.controller;

import com.synaptix.entity.IEntity;
import com.synaptix.widget.component.controller.IComponentsManagementController;

/**
 * Interface for CRUD controllers
 * 
 * @author Nicolas P
 * 
 * @param <E>
 */
public interface ICRUDManagementController<G extends IEntity> extends IComponentsManagementController<G> {

	/**
	 * Add entity
	 */
	public void addEntity();

	/**
	 * Edit a entity
	 * 
	 * @param paginationEntity
	 */
	public void editEntity(G paginationEntity);

	/**
	 * Show a entity
	 * 
	 * @param paginationEntity
	 */
	public void showEntity(G paginationEntity);

	/**
	 * Clone a entity
	 * 
	 * @param paginationEntity
	 */
	public void cloneEntity(G paginationEntity);

	/**
	 * Delete a entity
	 * 
	 * @param paginationEntity
	 */
	public void deleteEntity(G paginationEntity);

	/**
	 * Has authorisation to write
	 * 
	 * @return
	 */
	public boolean hasAuthWrite();

}
