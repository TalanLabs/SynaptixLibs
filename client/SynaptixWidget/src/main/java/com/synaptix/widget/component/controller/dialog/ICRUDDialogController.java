package com.synaptix.widget.component.controller.dialog;

import java.io.Serializable;

import com.synaptix.client.view.IView;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.entity.IEntity;
import com.synaptix.widget.crud.controller.ICRUDContext;

/**
 * A CRUD dialog controller
 *
 * @param <E>
 */
public interface ICRUDDialogController<E extends IEntity> {

	/**
	 * Show a dialog and add entity
	 *
	 * @param resultCallback
	 */
	public void addEntity(IView parentView, IResultCallback<E> resultCallback);

	/**
	 * Show a dialog and show entity
	 *
	 * @param resultCallback
	 */
	public void showEntity(IView parentView, E entity);

	/**
	 * Show a dialog and edit entity
	 *
	 * @param entity
	 * @param resultCallback
	 */
	public void editEntity(IView parentView, E entity, IResultCallback<E> resultCallback);

	/**
	 * Show a dialog and clone entity, use cleanEntityForClone for clean
	 *
	 * @param entity
	 * @param resultCallback
	 */
	public void cloneEntity(IView parentView, E entity, IResultCallback<E> resultCallback);

	/**
	 * Sets the crud context, used to browse entities and edit
	 */
	public void setCRUDContext(ICRUDContext<E> crudContext);

	public boolean hasPrevious(Serializable idCurrent);

	public boolean hasNext(Serializable idCurrent);

	public void showPrevious(Serializable idCurrent, boolean hasChanged);

	public void showNext(Serializable idCurrent, boolean hasChanged);

	public boolean hasAuthWrite();

	public void saveBean(IView parent);

	public void setSelectedTabItem(int selectedIndex);

	public int getSelectedTabItem();

	/**
	 * Are the two entities equals?
	 */
	public boolean hasChanged(E e1, E e2);

}
