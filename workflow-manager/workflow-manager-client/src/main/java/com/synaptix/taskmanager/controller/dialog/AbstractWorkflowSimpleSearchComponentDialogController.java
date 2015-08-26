package com.synaptix.taskmanager.controller.dialog;

import com.synaptix.component.IComponent;
import com.synaptix.service.IPaginationService;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.widget.component.controller.dialog.AbstractSimpleSearchComponentDialogController;
import com.synaptix.widget.view.ISynaptixViewFactory;

public abstract class AbstractWorkflowSimpleSearchComponentDialogController<V extends ISynaptixViewFactory, E extends IComponent> extends AbstractSimpleSearchComponentDialogController<V, E> {

	public AbstractWorkflowSimpleSearchComponentDialogController(V viewFactory, Class<E> componentClass, String title) {
		super(viewFactory, componentClass, title);
	}

	public AbstractWorkflowSimpleSearchComponentDialogController(V viewFactory, Class<E> componentClass, Class<? extends IPaginationService<E>> paginationServiceClass, String title) {
		super(viewFactory, componentClass, paginationServiceClass, title);
	}

	@Override
	protected final IServiceFactory getServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("psc-normal"); //$NON-NLS-1$
	}
}
