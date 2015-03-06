package com.synaptix.widget.component.controller.dialog;

import com.synaptix.entity.IEntity;
import com.synaptix.service.IPaginationService;
import com.synaptix.widget.view.ISynaptixViewFactory;

/**
 * 
 * @author Gaby
 * 
 * @param <V>
 *            View Factory
 * @param <E>
 *            Edit entity
 * @param <G>
 *            Pagination entity which
 */
public abstract class AbstractSearchEntityDialogController<V extends ISynaptixViewFactory, E extends IEntity, G extends IEntity> extends AbstractSearchComponentDialogController<V, E, G> {

	/**
	 * Constructs a <code>DefaultSearchComponentController</code> with multiple selection enabled and read only disabled.
	 * 
	 * @param viewFactory
	 * @param editComponentClass
	 * @param paginationComponentClass
	 * @param title
	 */
	public AbstractSearchEntityDialogController(V viewFactory, Class<E> editComponentClass, Class<G> paginationComponentClass, String title) {
		this(viewFactory, editComponentClass, paginationComponentClass, null, title);
	}

	/**
	 * 
	 * @param viewFactory
	 * @param editComponentClass
	 * @param paginationComponentClass
	 * @param paginationServiceClass
	 * @param title
	 */
	public AbstractSearchEntityDialogController(V viewFactory, Class<E> editComponentClass, Class<G> paginationComponentClass, Class<? extends IPaginationService<G>> paginationServiceClass,
			String title) {
		super(viewFactory, editComponentClass, paginationComponentClass, paginationServiceClass, title);
	}

	@Override
	protected E findFullComponent(G paginationEntity) {
		return getEntityService().findEntityById(editComponentClass, paginationEntity.getId());
	}
}
