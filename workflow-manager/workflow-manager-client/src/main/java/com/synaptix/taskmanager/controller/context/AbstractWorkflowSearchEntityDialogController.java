package com.synaptix.taskmanager.controller.context;

import com.synaptix.entity.IEntity;
import com.synaptix.service.IPaginationService;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.widget.component.controller.dialog.AbstractSimpleSearchEntityDialogController;
import com.synaptix.widget.view.ISynaptixViewFactory;

public abstract class AbstractWorkflowSearchEntityDialogController<V extends ISynaptixViewFactory, E extends IEntity> extends AbstractSimpleSearchEntityDialogController<V, E> {

	public AbstractWorkflowSearchEntityDialogController(V viewFactory, Class<E> componentClass, String title) {
		super(viewFactory, componentClass, title);
	}

	public AbstractWorkflowSearchEntityDialogController(V viewFactory, Class<E> componentClass, Class<? extends IPaginationService<E>> paginationServiceClass, String title) {
		super(viewFactory, componentClass, paginationServiceClass, title);
	}

	@Override
	protected final IServiceFactory getServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("psc-normal"); //$NON-NLS-1$
	}
}
