package com.synaptix.widget.component.controller.dialog;

import java.io.Serializable;

import com.synaptix.client.view.IView;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.entity.ICancellable;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.ITracable;
import com.synaptix.widget.crud.controller.ICRUDContext;
import com.synaptix.widget.view.dialog.IBeanDialogView;
import com.synaptix.widget.view.dialog.ICRUDBeanDialogView;

/**
 * An abstract CRUD Dialog Controller which allows the user to browse between the entities
 *
 * @param <E>
 */
public abstract class AbstractCRUDDialogController<E extends IEntity> implements ICRUDDialogController<E> {

	public enum CloseAction {

		/***/
		SHOW_PREVIOUS,
		/***/
		SHOW_NEXT;

	}

	private final Class<E> entityClass;

	private final String showTitle;

	private final String addTitle;

	private final String editTitle;

	private ICRUDContext<E> crudContext;

	private CloseAction closeAction;

	public AbstractCRUDDialogController(Class<E> entityClass, String showTitle, String addTitle, String editTitle) {
		super();
		this.entityClass = entityClass;
		this.showTitle = showTitle;
		this.addTitle = addTitle;
		this.editTitle = editTitle;
	}

	protected abstract ICRUDBeanDialogView<E> getCRUDBeanDialogView();

	@Override
	public void addEntity(IView parentView, IResultCallback<E> resultCallback) {
		E entity = ComponentFactory.getInstance().createInstance(entityClass);
		if (getCRUDBeanDialogView().showDialog(parentView, addTitle, null, entity, null, false, true) == IBeanDialogView.ACCEPT_OPTION) {
			resultCallback.setResult(getCRUDBeanDialogView().getBean());
		}
	}

	@Override
	public void showEntity(IView parentView, E entity) {
		getCRUDBeanDialogView().setCRUDDialogContext(this);
		getCRUDBeanDialogView().showDialog(parentView, showTitle, null, entity, null, !hasAuthWrite(), false);
	}

	@Override
	public void editEntity(IView parentView, E entity, IResultCallback<E> resultCallback) {
		if (getCRUDBeanDialogView().showDialog(parentView, editTitle, null, entity, null, false, false) == IBeanDialogView.ACCEPT_OPTION) {
			resultCallback.setResult(getCRUDBeanDialogView().getBean());
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
		if (ITracable.class.isAssignableFrom(entity.getClass())) {
			ITracable tracable = (ITracable) entity;
			tracable.setCreatedBy(null);
			tracable.setUpdatedBy(null);
			tracable.setCreatedDate(null);
			tracable.setUpdatedDate(null);
		}
		if (ICancellable.class.isAssignableFrom(entity.getClass())) {
			ICancellable cancellable = (ICancellable) entity;
			cancellable.setCancelBy(null);
			cancellable.setCancelDate(null);
			cancellable.setCheckCancel(false);
		}
	}

	@Override
	public void cloneEntity(IView parentView, E entity, IResultCallback<E> resultCallback) {
		E entity2 = ComponentHelper.clone(entity);
		cleanEntityForClone(entity2);
		if (getCRUDBeanDialogView().showDialog(parentView, addTitle, null, entity2, null, false, false) == IBeanDialogView.ACCEPT_OPTION) {
			resultCallback.setResult(getCRUDBeanDialogView().getBean());
		}
	}

	@Override
	public void setCRUDContext(ICRUDContext<E> crudContext) {
		this.crudContext = crudContext;
	}

	@Override
	public boolean hasPrevious(Serializable idCurrent) {
		return crudContext != null && crudContext.hasPrevious(idCurrent);
	}

	@Override
	public boolean hasNext(Serializable idCurrent) {
		return crudContext != null && crudContext.hasNext(idCurrent);
	}

	@Override
	public void showPrevious(Serializable idCurrent, boolean hasChanged) {
		if (crudContext != null) {
			closeAction = CloseAction.SHOW_PREVIOUS;
			if ((hasChanged) && (crudContext.askSaveChanges(getCRUDBeanDialogView()))) {
				getCRUDBeanDialogView().accept(true);
			} else {
				getCRUDBeanDialogView().closeDialog();
				crudContext.showPrevious(idCurrent);
			}
		}
	}

	@Override
	public void showNext(Serializable idCurrent, boolean hasChanged) {
		if (crudContext != null) {
			closeAction = CloseAction.SHOW_NEXT;
			if ((hasChanged) && (crudContext.askSaveChanges(getCRUDBeanDialogView()))) {
				getCRUDBeanDialogView().accept(true);
			} else {
				getCRUDBeanDialogView().closeDialog();
				crudContext.showNext(idCurrent);
			}
		}
	}

	@Override
	public boolean hasAuthWrite() {
		if (crudContext != null) {
			return crudContext.hasAuthWrite();
		}
		return false;
	}

	@Override
	public void saveBean(IView parent) {
		if (crudContext != null) {
			crudContext.saveBean(getCRUDBeanDialogView().getBean(), parent, closeAction);
		}
	}

	@Override
	public void setSelectedTabItem(int selectedTabIndex) {
		if (crudContext != null) {
			crudContext.setSelectedTabIndex(selectedTabIndex);
		}
	}

	@Override
	public int getSelectedTabItem() {
		if (crudContext != null) {
			return crudContext.getSelectedTabIndex();
		}
		return 0;
	}
}
