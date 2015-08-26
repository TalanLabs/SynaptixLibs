package com.synaptix.taskmanager.controller.context;

import com.synaptix.component.IComponent;
import com.synaptix.service.IPaginationService;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.widget.component.controller.context.AbstractSearchComponentsContext;
import com.synaptix.widget.view.ISynaptixViewFactory;

public abstract class AbstractPscSearchComponentsContext<V extends ISynaptixViewFactory, E extends IComponent> extends AbstractSearchComponentsContext<V, E> {

	public AbstractPscSearchComponentsContext(V viewFactory, Class<E> componentClass) {
		super(viewFactory, componentClass);
	}

	public AbstractPscSearchComponentsContext(V viewFactory, Class<E> componentClass, Class<? extends IPaginationService<E>> paginationServiceClass) {
		super(viewFactory, componentClass, paginationServiceClass);
	}

	public AbstractPscSearchComponentsContext(V viewFactory, Class<E> componentClass, int defaultSizePage) {
		super(viewFactory, componentClass, defaultSizePage);
	}

	public AbstractPscSearchComponentsContext(V viewFactory, Class<E> componentClass, Class<? extends IPaginationService<E>> paginationServiceClass, int defaultSizePage) {
		super(viewFactory, componentClass, paginationServiceClass, defaultSizePage);
	}

	@Override
	protected final IServiceFactory getServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("psc-normal"); //$NON-NLS-1$
	}
}
