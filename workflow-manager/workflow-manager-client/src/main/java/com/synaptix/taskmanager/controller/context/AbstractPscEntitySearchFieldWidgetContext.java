package com.synaptix.taskmanager.controller.context;

import java.util.Map;

import com.synaptix.client.view.IView;
import com.synaptix.entity.IEntity;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.widget.component.controller.context.AbstractEntitySearchFieldWidgetContext;
import com.synaptix.widget.view.ISynaptixViewFactory;

public abstract class AbstractPscEntitySearchFieldWidgetContext<V extends ISynaptixViewFactory, E extends IEntity> extends AbstractEntitySearchFieldWidgetContext<V, E> {

	public AbstractPscEntitySearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, String... filterPropertyNames) {
		super(viewFactory, parent, componentClass, filterPropertyNames);
	}

	public AbstractPscEntitySearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, Map<String, Object> initialValueFilterMap, String... filterPropertyNames) {
		super(viewFactory, parent, componentClass, initialValueFilterMap, filterPropertyNames);
	}

	public AbstractPscEntitySearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, String[] filterPropertyNames, String[] suggestColumns) {
		super(viewFactory, parent, componentClass, null, filterPropertyNames, suggestColumns);
	}

	public AbstractPscEntitySearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, Map<String, Object> initialValueFilterMap, String[] filterPropertyNames,
			String[] suggestColumns) {
		super(viewFactory, parent, componentClass, initialValueFilterMap, filterPropertyNames, suggestColumns);
	}

	@Override
	protected final IServiceFactory getServiceFactory() {
		return ServicesManager.getInstance().getServiceFactory("psc-normal"); //$NON-NLS-1$
	}
}
