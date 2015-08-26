package com.synaptix.taskmanager.controller.dialog;

import com.synaptix.entity.IEntity;
import com.synaptix.service.IPaginationService;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.widget.component.controller.dialog.AbstractSearchEntityDialogController;
import com.synaptix.widget.view.ISynaptixViewFactory;

public abstract class AbstractPscSearchEntityDialogController<V extends ISynaptixViewFactory, E extends IEntity, G extends IEntity> extends AbstractSearchEntityDialogController<V, E, G> {

	public AbstractPscSearchEntityDialogController(V viewFactory, Class<E> editComponentClass, Class<G> paginationComponentClass, String title) {
		super(viewFactory, editComponentClass, paginationComponentClass, title);
	}

	public AbstractPscSearchEntityDialogController(V viewFactory, Class<E> editComponentClass, Class<G> paginationComponentClass, Class<? extends IPaginationService<G>> paginationServiceClass,
			String title) {
		super(viewFactory, editComponentClass, paginationComponentClass, paginationServiceClass, title);
	}

	@Override
	protected final IServiceFactory getServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("psc-normal"); //$NON-NLS-1$
	}
}
