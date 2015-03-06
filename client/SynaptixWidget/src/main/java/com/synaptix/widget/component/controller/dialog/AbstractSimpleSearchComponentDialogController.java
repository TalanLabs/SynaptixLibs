package com.synaptix.widget.component.controller.dialog;

import com.synaptix.component.IComponent;
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
public abstract class AbstractSimpleSearchComponentDialogController<V extends ISynaptixViewFactory, E extends IComponent> extends AbstractSearchComponentDialogController<V, E, E> {

	/**
	 * Constructs a <code>AbstractSearchComponentDialogController</code> with multiple selection enabled and read only disabled.
	 * 
	 * @param viewFactory
	 * @param componentClass
	 * @param title
	 */
	public AbstractSimpleSearchComponentDialogController(V viewFactory, Class<E> componentClass, String title) {
		this(viewFactory, componentClass, null, title);
	}

	/**
	 * 
	 * @param viewFactory
	 * @param componentClass
	 * @param paginationServiceClass
	 * @param title
	 */
	public AbstractSimpleSearchComponentDialogController(V viewFactory, Class<E> componentClass, Class<? extends IPaginationService<E>> paginationServiceClass, String title) {
		super(viewFactory, componentClass, componentClass, paginationServiceClass, title);
	}
}
