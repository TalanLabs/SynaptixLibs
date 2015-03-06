package com.synaptix.widget.component.controller.context;

import java.util.List;
import java.util.Map;

import com.synaptix.client.view.IView;
import com.synaptix.component.IComponent;
import com.synaptix.widget.view.ISynaptixViewFactory;

public abstract class AbstractComponentSearchFieldWidgetContext<V extends ISynaptixViewFactory, E extends IComponent> extends AbstractComponentExtendedSearchFieldWidgetContext<V, E, E> {

	/**
	 * 
	 * @param viewFactory
	 * @param parent
	 * @param componentClass
	 * @param filterPropertyNames
	 *            use for filter and sort
	 */
	public AbstractComponentSearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, String... filterPropertyNames) {
		this(viewFactory, parent, componentClass, null, filterPropertyNames, null);
	}

	/**
	 * 
	 * @param viewFactory
	 * @param parent
	 * @param componentClass
	 * @param filterPropertyNames
	 *            use for filter and sort
	 * @param suggestColumns
	 */
	public AbstractComponentSearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, String[] filterPropertyNames, String[] suggestColumns) {
		this(viewFactory, parent, componentClass, null, filterPropertyNames, suggestColumns);
	}

	/**
	 * 
	 * @param viewFactory
	 * @param parent
	 * @param componentClass
	 * @param initialValueFilterMap
	 * @param filterPropertyNames
	 *            use for filter and sort
	 */
	public AbstractComponentSearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, Map<String, Object> initialValueFilterMap, String... filterPropertyNames) {
		super(viewFactory, parent, componentClass, componentClass, initialValueFilterMap, filterPropertyNames);
	}

	/**
	 * 
	 * @param viewFactory
	 * @param parent
	 * @param componentClass
	 * @param initialValueFilterMap
	 * @param filterPropertyNames
	 *            use for filter and sort
	 * @param suggestColumns
	 */
	public AbstractComponentSearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, Map<String, Object> initialValueFilterMap, String[] filterPropertyNames,
			String[] suggestColumns) {
		super(viewFactory, parent, componentClass, componentClass, initialValueFilterMap, filterPropertyNames, suggestColumns);
	}

	@Override
	protected List<E> convert(List<E> list) {
		return list;
	}
}
