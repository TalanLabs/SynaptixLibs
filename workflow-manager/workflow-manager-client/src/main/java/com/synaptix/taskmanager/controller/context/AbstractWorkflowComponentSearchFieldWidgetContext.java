package com.synaptix.taskmanager.controller.context;

import java.util.Map;

import com.synaptix.client.view.IView;
import com.synaptix.component.IComponent;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.widget.component.controller.context.AbstractComponentSearchFieldWidgetContext;
import com.synaptix.widget.view.ISynaptixViewFactory;

/**
 * Context for search field widget.
 * 
 * @param <V>
 *            View factory
 * @param <E>
 *            Component
 */
public abstract class AbstractWorkflowComponentSearchFieldWidgetContext<V extends ISynaptixViewFactory, E extends IComponent> extends AbstractComponentSearchFieldWidgetContext<V, E> {

	public AbstractWorkflowComponentSearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, String... filterPropertyNames) {
		super(viewFactory, parent, componentClass, filterPropertyNames);
	}

	public AbstractWorkflowComponentSearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, String[] filterPropertyNames, String[] suggestColumns) {
		super(viewFactory, parent, componentClass, filterPropertyNames, suggestColumns);
	}

	public AbstractWorkflowComponentSearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, Map<String, Object> initialValueFilterMap, String... filterPropertyNames) {
		super(viewFactory, parent, componentClass, initialValueFilterMap, filterPropertyNames);
	}

	@Override
	protected final IServiceFactory getServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("psc-normal"); //$NON-NLS-1$
	}
}
