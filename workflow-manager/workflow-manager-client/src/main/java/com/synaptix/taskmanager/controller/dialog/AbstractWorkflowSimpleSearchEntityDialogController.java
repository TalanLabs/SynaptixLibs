package com.synaptix.taskmanager.controller.dialog;

import com.synaptix.entity.IEntity;
import com.synaptix.service.IPaginationService;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.widget.component.controller.dialog.AbstractSimpleSearchEntityDialogController;
import com.synaptix.widget.view.ISynaptixViewFactory;

public abstract class AbstractWorkflowSimpleSearchEntityDialogController<V extends ISynaptixViewFactory, E extends IEntity> extends AbstractSimpleSearchEntityDialogController<V, E> {

	public AbstractWorkflowSimpleSearchEntityDialogController(V viewFactory, Class<E> componentClass, String title) {
		super(viewFactory, componentClass, title);
	}

	public AbstractWorkflowSimpleSearchEntityDialogController(V viewFactory, Class<E> componentClass, Class<? extends IPaginationService<E>> paginationServiceClass, String title) {
		super(viewFactory, componentClass, paginationServiceClass, title);
	}

	@Override
	protected final IServiceFactory getServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("psc-normal"); //$NON-NLS-1$
	}
}
