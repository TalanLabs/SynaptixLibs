package com.synaptix.widget.component.controller.dialog.context;

import java.util.Map;

import com.synaptix.client.view.IView;
import com.synaptix.component.IComponent;
import com.synaptix.widget.view.ISynaptixViewFactory;

public abstract class AbstractComponentSearchDialogContext<V extends ISynaptixViewFactory, E extends IComponent> extends AbstractComponentExtendedSearchDialogContext<V, E, E> {

	/**
	 * 
	 * @param viewFactory
	 * @param parent
	 */
	public AbstractComponentSearchDialogContext(V viewFactory, IView parent, Class<E> componentClass) {
		super(viewFactory, parent, componentClass, componentClass);
	}

	/**
	 * 
	 * @param viewFactory
	 * @param parent
	 * @param initialValueFilterMap
	 */
	public AbstractComponentSearchDialogContext(V viewFactory, IView parent, Class<E> componentClass, Map<String, Object> initialValueFilterMap) {
		super(viewFactory, parent, componentClass, componentClass, initialValueFilterMap);
	}
}
