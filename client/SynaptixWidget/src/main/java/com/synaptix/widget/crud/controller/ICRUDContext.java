package com.synaptix.widget.crud.controller;

import com.synaptix.client.view.IView;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
import com.synaptix.widget.component.controller.dialog.AbstractCRUDDialogController;

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
	public boolean hasPrevious(IId id);

	/**
	 * Does the entity have a next one in the current list?
	 */
	public boolean hasNext(IId id);

	/**
	 * Shows the previous entity from the current list
	 */
	public void showPrevious(IId id);

	/**
	 * Shows the next entity from the current list
	 */
	public void showNext(IId id);

	/**
	 * Saves the bean (without closing the window, done prior to this if needed) and does the following action if provided
	 */
	public void saveBean(E bean, IView parent, AbstractCRUDDialogController.CloseAction closeAction);

	/**
	 * Has authorisation to write (copy from ICRUDManagementController)
	 */
	public boolean hasAuthWrite();

	public void setSelectedTabIndex(int selectedTabIndex);

	/**
	 * The last selected tab
	 */
	public int getSelectedTabIndex();

	/**
	 * There are changes. Ask the user if he wants to save them.
	 */
	public boolean askSaveChanges(IView parent);

}
