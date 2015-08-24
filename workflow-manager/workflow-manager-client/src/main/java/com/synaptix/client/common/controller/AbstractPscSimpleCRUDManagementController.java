package com.synaptix.client.common.controller;

import com.synaptix.entity.IEntity;
import com.synaptix.service.ICRUDEntityService;
import com.synaptix.service.IPaginationService;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.widget.crud.controller.AbstractSimpleCRUDManagementController;
import com.synaptix.widget.view.ISynaptixViewFactory;

public abstract class AbstractPscSimpleCRUDManagementController<V extends ISynaptixViewFactory, E extends IEntity> extends AbstractSimpleCRUDManagementController<V, E> {

	public AbstractPscSimpleCRUDManagementController(V viewFactory, Class<E> componentClass) {
		super(viewFactory, componentClass);
	}

	public AbstractPscSimpleCRUDManagementController(V viewFactory, Class<E> componentClass, Class<? extends ICRUDEntityService<E>> crudEntityServiceClass,
			Class<? extends IPaginationService<E>> paginationServiceClass) {
		super(viewFactory, componentClass, crudEntityServiceClass, paginationServiceClass);
	}

	@Override
	protected final IServiceFactory getServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("psc-normal"); //$NON-NLS-1$
	}
}
