package com.synaptix.widget.component.controller.dialog;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;

import com.synaptix.client.view.IView;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.entity.ICancellable;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.IId;
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
	 * @param entity
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
	public boolean hasPrevious(IId idCurrent) {
		return crudContext != null && crudContext.hasPrevious(idCurrent);
	}

	@Override
	public boolean hasNext(IId idCurrent) {
		return crudContext != null && crudContext.hasNext(idCurrent);
	}

	@Override
	public void showPrevious(IId idCurrent, boolean hasChanged) {
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
	public void showNext(IId idCurrent, boolean hasChanged) {
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

	@Override
	public boolean hasChanged(E e1, E e2) {
		cleanEntity(e1);
		cleanEntity(e2);
		return !equalComponent(e1, e2);
	}

	private boolean equalComponent(IComponent c1, IComponent c2) {
		return equalMaps(c1.straightGetProperties(), c2.straightGetProperties());
	}

	private boolean equalMaps(Map<?, ?> m1, Map<?, ?> m2) {
		if (CollectionHelper.size(m1) != CollectionHelper.size(m2)) {
			return false;
		}
		for (Object key : m1.keySet()) {
			Object v1 = m1.get(key);
			Object v2 = m2.get(key);
			if (!equal(v1, v2)) {
				return false;
			}
		}
		return true;
	}

	private boolean equalCollection(Collection<?> m1, Collection<?> m2) {
		if (CollectionHelper.size(m1) != CollectionHelper.size(m2)) {
			return false;
		}
		Iterator<?> ite1 = m1.iterator();
		Iterator<?> ite2 = m2.iterator();
		while (ite1.hasNext()) {
			Object v1 = ite1.next();
			Object v2 = ite2.next();
			if (!equal(v1, v2)) {
				return false;
			}
		}
		return true;
	}

	private boolean equal(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if ((o1 instanceof IComponent) && (o2 instanceof IComponent)) {
			return equalComponent((IComponent) o1, (IComponent) o2);
		}
		if ((o1 instanceof Map) && (o2 instanceof Map)) {
			return equalMaps((Map<?, ?>) o1, (Map<?, ?>) o2);
		}
		if ((o1 instanceof byte[]) && (o2 instanceof byte[])) {
			return Arrays.equals((byte[]) o1, (byte[]) o2);
		}
		if ((o1 instanceof Collection) && (o2 instanceof Collection)) {
			return equalCollection((Collection<?>) o1, (Collection<?>) o2);
		}
		return ObjectUtils.equals(o1, o2);
	}
}
