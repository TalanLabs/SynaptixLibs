package com.synaptix.widget.crud.controller;

import java.io.Serializable;

import com.synaptix.client.view.IView;
import com.synaptix.entity.IEntity;

/**
 * A CRUD context which allows the user to browse between the entities
 *
 * @author NicolasP
 *
 * @param <E>
 */
public interface ICRUDContext<E extends IEntity> {

	/**
	 * Does the entity have a previoous one in the current list?
	 */
	public boolean hasPrevious(Serializable id);

	/**
	 * Does the entity have a next one in the current list?
	 */
	public boolean hasNext(Serializable id);

	/**
	 * Shows the previous entity from the current list
	 */
	public void showPrevious(Serializable id);

	/**
	 * Shows the next entity from the current list
	 */
	public void showNext(Serializable id);

	/**
	 * Saves the bean (without closing the window)
	 */
	public void saveBean(E bean, IView parent);

	/**
	 * Has authorisation to write (copy from ICRUDManagementController)
	 */
	public boolean hasAuthWrite();

	public void setSelectedTabIndex(int selectedTabIndex);

	/**
	 * The last selected tab
	 */
	public int getSelectedTabIndex();

}
