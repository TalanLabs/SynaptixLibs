package com.synaptix.widget.crud.controller;

import com.synaptix.component.IComponent;
import com.synaptix.service.IPaginationService;
import com.synaptix.widget.component.controller.IComponentsManagementController;
import com.synaptix.widget.component.controller.context.AbstractSearchComponentsContext;
import com.synaptix.widget.component.view.IComponentsManagementView;
import com.synaptix.widget.component.view.IComponentsManagementViewDescriptor;
import com.synaptix.widget.component.view.ISearchTablePageComponentsView;
import com.synaptix.widget.view.ISynaptixViewFactory;
import com.synaptix.widget.view.IViewDescriptor;

/**
 * Create a simple table with filter components search
 * 
 * @param <V>
 *            View factory
 * @param <E>
 *            Pagination component
 */
public abstract class AbstractComponentsManagementController<V extends ISynaptixViewFactory, E extends IComponent> extends AbstractSearchComponentsContext<V, E> implements
		IComponentsManagementController<E> {

	public AbstractComponentsManagementController(V viewFactory, Class<E> componentClass) {
		this(viewFactory, componentClass, null);
	}

	public AbstractComponentsManagementController(V viewFactory, Class<E> componentClass, Class<? extends IPaginationService<E>> paginationServiceClass) {
		super(viewFactory, componentClass, paginationServiceClass);
	}

	@Override
	protected final ISearchTablePageComponentsView<E> createSearchComponentsView() {
		return createComponentsManagementView();
	}

	/**
	 * Create components management view. NOT CALL use getComponentsManagementView()
	 * 
	 * @return
	 */
	protected IComponentsManagementView<E> createComponentsManagementView() {
		return getViewFactory().newComponentsManagementView(componentClass, getViewDescriptor());
	}

	/**
	 * Get a components management view
	 * 
	 * @return
	 */
	protected final IComponentsManagementView<E> getComponentsManagementView() {
		return (IComponentsManagementView<E>) getSearchComponentsView();
	}

	/**
	 * Create a components management view descriptor. NOT CALL use getComponentsManagementViewDescriptor()
	 * 
	 * @return
	 */
	protected abstract IComponentsManagementViewDescriptor<E> createComponentsManagementViewDescriptor();

	@Override
	protected final IViewDescriptor<E> createViewDescriptor() {
		return createComponentsManagementViewDescriptor();
	}

	/**
	 * Get a components management view descriptor
	 * 
	 * @return
	 */
	protected final IComponentsManagementViewDescriptor<E> getComponentsManagementViewDescriptor() {
		return (IComponentsManagementViewDescriptor<E>) getViewDescriptor();
	}

	@Override
	public String getId() {
		return this.getClass().getName() + "_" + componentClass.getName();
	}

	@Override
	public String getName() {
		return getId();
	}

	@Override
	public void start() {
	}

	@Override
	public boolean stop() {
		return true;
	}
}
