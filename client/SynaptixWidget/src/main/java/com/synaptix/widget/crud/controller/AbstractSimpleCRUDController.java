package com.synaptix.widget.crud.controller;

import com.synaptix.entity.IEntity;
import com.synaptix.service.ICRUDEntityService;
import com.synaptix.widget.view.ISynaptixViewFactory;

public abstract class AbstractSimpleCRUDController<V extends ISynaptixViewFactory, E extends IEntity> extends AbstractCRUDController<V, E, E> {

	public AbstractSimpleCRUDController(V viewFactory, Class<E> crudComponentClass) {
		this(viewFactory, crudComponentClass, null);
	}

	public AbstractSimpleCRUDController(V viewFactory, Class<E> crudComponentClass, Class<? extends ICRUDEntityService<E>> crudEntityServiceClass) {
		super(viewFactory, crudComponentClass, crudComponentClass, crudEntityServiceClass);
	}
}
