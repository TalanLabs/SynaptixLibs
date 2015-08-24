package com.synaptix.client.common.controller;

import com.synaptix.entity.IEntity;
import com.synaptix.service.ICRUDEntityService;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.widget.crud.controller.AbstractSimpleCRUDController;
import com.synaptix.widget.view.ISynaptixViewFactory;

public abstract class AbstractWorkflowSimpleCRUDController<V extends ISynaptixViewFactory, E extends IEntity> extends AbstractSimpleCRUDController<V, E> {

	public AbstractWorkflowSimpleCRUDController(V viewFactory, Class<E> crudComponentClass) {
		super(viewFactory, crudComponentClass);
	}

	public AbstractWorkflowSimpleCRUDController(V viewFactory, Class<E> crudComponentClass, Class<? extends ICRUDEntityService<E>> crudEntityServiceClass) {
		super(viewFactory, crudComponentClass, crudEntityServiceClass);
	}

	@Override
	protected final IServiceFactory getServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("psc-normal"); //$NON-NLS-1$
	}
}
