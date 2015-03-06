package com.synaptix.widget.crud.controller;

import com.synaptix.entity.IEntity;
import com.synaptix.service.ICRUDEntityService;
import com.synaptix.service.IPaginationService;
import com.synaptix.widget.view.ISynaptixViewFactory;

public abstract class AbstractSimpleCRUDWithChildrenManagementController<V extends ISynaptixViewFactory, E extends IEntity, C extends IEntity> extends
		AbstractCRUDWithChildrenManagementController<V, E, E, C> {

	public AbstractSimpleCRUDWithChildrenManagementController(V viewFactory, Class<E> componentClass, Class<C> childClass, String idParentPropertyName) {
		this(viewFactory, componentClass, childClass, idParentPropertyName, null, null, null);
	}

	public AbstractSimpleCRUDWithChildrenManagementController(V viewFactory, Class<E> componentClass, Class<C> childClass, String idParentPropertyName,
			Class<? extends ICRUDEntityService<E>> crudEntityServiceClass, Class<? extends IPaginationService<E>> paginationServiceClass,
			Class<? extends ICRUDEntityService<C>> crudChildEntityServiceClass) {
		super(viewFactory, componentClass, componentClass, childClass, idParentPropertyName, crudEntityServiceClass, paginationServiceClass, crudChildEntityServiceClass);
	}
}
