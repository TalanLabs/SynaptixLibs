package com.synaptix.client.common.controller;

import com.synaptix.component.IComponent;
import com.synaptix.service.IPaginationService;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.widget.crud.controller.AbstractComponentsManagementController;
import com.synaptix.widget.view.ISynaptixViewFactory;

public abstract class AbstractWorkflowComponentsManagementController<V extends ISynaptixViewFactory, E extends IComponent> extends AbstractComponentsManagementController<V, E> {

	public AbstractWorkflowComponentsManagementController(V viewFactory, Class<E> componentClass) {
		super(viewFactory, componentClass);
	}

	public AbstractWorkflowComponentsManagementController(V viewFactory, Class<E> componentClass, Class<? extends IPaginationService<E>> paginationServiceClass) {
		super(viewFactory, componentClass, paginationServiceClass);
	}

	@Override
	protected final IServiceFactory getServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("psc-normal"); //$NON-NLS-1$
	}
}
