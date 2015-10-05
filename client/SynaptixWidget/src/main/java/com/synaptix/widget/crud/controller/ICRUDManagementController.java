package com.synaptix.widget.crud.controller;

import java.util.List;

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
	 * @deprecated replaced by show entity, then edit from the dialog
	 */
	@Deprecated
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
	 * Delete many entities (only called if isMultiCancel returns true)
	 *
	 * @param paginationEntity
	 */
	public void deleteEntities(List<G> paginationEntityList);

	/**
	 * Has authorisation to write
	 *
	 * @return
	 */
	public boolean hasAuthWrite();

}
