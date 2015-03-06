package com.synaptix.widget.component.controller.dialog;

import com.synaptix.client.view.IView;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.ITracable;
import com.synaptix.widget.view.dialog.IBeanDialogView;

public abstract class AbstractCRUDDialogController<E extends IEntity> implements ICRUDDialogController<E> {

	private Class<E> entityClass;

	private String showTitle;

	private String addTitle;

	private String editTitle;

	public AbstractCRUDDialogController(Class<E> entityClass, String showTitle, String addTitle, String editTitle) {
		super();
		this.entityClass = entityClass;
		this.showTitle = showTitle;
		this.addTitle = addTitle;
		this.editTitle = editTitle;
	}

	protected abstract IBeanDialogView<E> getBeanDialogView();

	@Override
	public void addEntity(IView parentView, IResultCallback<E> resultCallback) {
		E entity = ComponentFactory.getInstance().createInstance(entityClass);
		if (getBeanDialogView().showDialog(parentView, addTitle, null, entity, null, false, true) == IBeanDialogView.ACCEPT_OPTION) {
			resultCallback.setResult(getBeanDialogView().getBean());
		}
	}

	@Override
	public void showEntity(IView parentView, E entity) {
		getBeanDialogView().showDialog(parentView, showTitle, null, entity, null, true, false);
	}

	@Override
	public void editEntity(IView parentView, E entity, IResultCallback<E> resultCallback) {
		if (getBeanDialogView().showDialog(parentView, editTitle, null, entity, null, false, false) == IBeanDialogView.ACCEPT_OPTION) {
			resultCallback.setResult(getBeanDialogView().getBean());
		}
	}

	/**
	 * Clean entity for clone
	 * 
	 * @param childEntity
	 */
	protected void cleanEntityForClone(E entity) {
		cleanEntity(entity);
	}

	protected final void cleanEntity(IEntity entity) {
		entity.setId(null);
		entity.setVersion(null);
		if (entity instanceof ITracable) {
			ITracable t = (ITracable) entity;
			t.setCreatedBy(null);
			t.setCreatedDate(null);
			t.setUpdatedBy(null);
			t.setUpdatedDate(null);
		}
	}

	@Override
	public void cloneEntity(IView parentView, E entity, IResultCallback<E> resultCallback) {
		E entity2 = ComponentHelper.clone(entity);
		cleanEntityForClone(entity2);
		if (getBeanDialogView().showDialog(parentView, addTitle, null, entity2, null, false, false) == IBeanDialogView.ACCEPT_OPTION) {
			resultCallback.setResult(getBeanDialogView().getBean());
		}
	}
}
