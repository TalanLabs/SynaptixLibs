package com.synaptix.widget.component.controller.dialog.edit;

import com.synaptix.client.view.IView;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.component.IComponent;

public interface ICRUDBeanExtensionDialogContext<E extends IComponent> {

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

}
