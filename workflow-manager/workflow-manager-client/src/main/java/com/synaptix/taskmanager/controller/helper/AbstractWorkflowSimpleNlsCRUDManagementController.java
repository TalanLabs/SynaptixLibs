package com.synaptix.taskmanager.controller.helper;

import com.synaptix.client.common.controller.DefaultNlsMessageExtendedContext;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.INlsMessage;
import com.synaptix.service.ICRUDEntityService;
import com.synaptix.service.IPaginationService;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.taskmanager.view.ICommonViewFactory;
import com.synaptix.widget.crud.controller.AbstractSimpleCRUDManagementController;

/**
 * A simple CRUD displaying the same object than the one which can be added/edited/removed
 * 
 * @author Nicolas P
 * 
 * @param <V>
 *            View factory
 * @param <E>
 *            CRUD and pagination Entity
 */
public abstract class AbstractWorkflowSimpleNlsCRUDManagementController<V extends ICommonViewFactory, E extends IEntity & INlsMessage> extends AbstractSimpleCRUDManagementController<V, E>
		implements
		INlsCRUDManagementController<E> {

	private INlsMessageExtendedContext nlsMessageExtendedContext;

	public AbstractWorkflowSimpleNlsCRUDManagementController(V viewFactory, Class<E> componentClass) {
		this(viewFactory, componentClass, null, null);
	}

	public AbstractWorkflowSimpleNlsCRUDManagementController(V viewFactory, Class<E> componentClass, Class<? extends ICRUDEntityService<E>> crudEntityServiceClass,
			Class<? extends IPaginationService<E>> paginationServiceClass) {
		super(viewFactory, componentClass, crudEntityServiceClass, paginationServiceClass);

		this.nlsMessageExtendedContext = new DefaultNlsMessageExtendedContext(viewFactory, this, componentClass);
	}

	@Override
	protected final IServiceFactory getServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("psc-normal"); //$NON-NLS-1$
	}

	protected INlsMessageExtendedContext getNlsMessageExtendedContext() {
		return nlsMessageExtendedContext;
	}

	@Override
	public final boolean hasAuthChangeNlsMeanings() {
		return getNlsMessageExtendedContext().hasAuthChangeNlsMeanings();
	}

	@Override
	public final void exportNlsMeanings() {
		getNlsMessageExtendedContext().exportNlsMeanings();
	}

	@Override
	public final void importNlsMeanings() {
		getNlsMessageExtendedContext().importNlsMeanings();
	}
}
