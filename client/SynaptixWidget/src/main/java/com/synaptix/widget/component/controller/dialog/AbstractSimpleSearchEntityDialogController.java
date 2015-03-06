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
 *            edit and pagination entity
 */
public abstract class AbstractSimpleSearchEntityDialogController<V extends ISynaptixViewFactory, E extends IEntity> extends AbstractSearchEntityDialogController<V, E, E> {

	/**
	 * Constructs a <code>AbstractSearchComponentDialogController</code> with multiple selection enabled and read only disabled.
	 * 
	 * @param viewFactory
	 * @param componentClass
	 * @param title
	 */
	public AbstractSimpleSearchEntityDialogController(V viewFactory, Class<E> componentClass, String title) {
		this(viewFactory, componentClass, null, title);
	}

	/**
	 * 
	 * @param viewFactory
	 * @param componentClass
	 * @param paginationServiceClass
	 * @param title
	 */
	public AbstractSimpleSearchEntityDialogController(V viewFactory, Class<E> componentClass, Class<? extends IPaginationService<E>> paginationServiceClass, String title) {
		super(viewFactory, componentClass, componentClass, paginationServiceClass, title);
	}
}
