package com.synaptix.widget.crud.controller;

import com.synaptix.entity.IEntity;
import com.synaptix.service.ICRUDEntityService;
import com.synaptix.service.IPaginationService;
import com.synaptix.widget.view.ISynaptixViewFactory;

/**
 * A simple CRUD displaying the same object than the one which can be added/edited/removed
 * 
 * @author Nicolas P
 * 
 * @param <V>
 *            View factory
 * @param <E>
 *            CRUD and pagination Entity
 */
public abstract class AbstractSimpleCRUDManagementController<V extends ISynaptixViewFactory, E extends IEntity> extends AbstractCRUDManagementController<V, E, E> {

	public AbstractSimpleCRUDManagementController(V viewFactory, Class<E> componentClass) {
		this(viewFactory, componentClass, null, null);
	}

	public AbstractSimpleCRUDManagementController(V viewFactory, Class<E> componentClass, Class<? extends ICRUDEntityService<E>> crudEntityServiceClass,
			Class<? extends IPaginationService<E>> paginationServiceClass) {
		super(viewFactory, componentClass, componentClass, crudEntityServiceClass, paginationServiceClass);
	}
}
