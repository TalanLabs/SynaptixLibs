package com.synaptix.widget.component.controller.dialog.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synaptix.client.view.IView;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.component.IComponent;
import com.synaptix.entity.CancellableFields;
import com.synaptix.entity.ICancellable;
import com.synaptix.widget.component.controller.context.ISearchDialogContext;
import com.synaptix.widget.component.controller.dialog.AbstractSearchComponentDialogController;
import com.synaptix.widget.view.ISynaptixViewFactory;

public abstract class AbstractComponentExtendedSearchDialogContext<V extends ISynaptixViewFactory, E extends IComponent, G extends E> implements ISearchDialogContext<E> {

	protected final V viewFactory;

	protected final IView parent;

	protected final Class<E> componentClass;

	protected final Class<G> componentExtendedClass;

	protected final Map<String, Object> initialValueFilterMap;

	/**
	 * 
	 * @param viewFactory
	 * @param parent
	 */
	public AbstractComponentExtendedSearchDialogContext(V viewFactory, IView parent, Class<E> componentClass, Class<G> componentExtendedClass) {
		this(viewFactory, parent, componentClass, componentExtendedClass, null);
	}

	/**
	 * 
	 * @param viewFactory
	 * @param parent
	 * @param initialValueFilterMap
	 */
	public AbstractComponentExtendedSearchDialogContext(V viewFactory, IView parent, Class<E> componentClass, Class<G> componentExtendedClass, Map<String, Object> initialValueFilterMap) {
		super();
		this.viewFactory = viewFactory;
		this.parent = parent;
		this.componentClass = componentClass;
		this.componentExtendedClass = componentExtendedClass;
		this.initialValueFilterMap = initialValueFilterMap;
	}

	/**
	 * Some filters can be added here
	 * 
	 * @param filters
	 */
	protected void completeFilters(final Map<String, Object> filters) {
		if (ICancellable.class.isAssignableFrom(componentClass)) {
			filters.put(CancellableFields.checkCancel().name(), false);
		}
	}

	protected abstract AbstractSearchComponentDialogController<V, E, G> createDefaultSearchComponentDialogController(V viewFactory, Map<String, Object> initialValueFilterMap);

	@Override
	public void searchOne(final IResultCallback<E> resultCallback) {
		Map<String, Object> valueFilterMap = new HashMap<String, Object>();
		if (initialValueFilterMap != null) {
			valueFilterMap.putAll(initialValueFilterMap);
		}
		completeFilters(valueFilterMap);
		AbstractSearchComponentDialogController<V, E, G> defaultSearchComponentDialogController = createDefaultSearchComponentDialogController(viewFactory, initialValueFilterMap);
		IResultCallback<List<E>> rc = new IResultCallback<List<E>>() {
			@Override
			public void setResult(List<E> e) {
				if (CollectionHelper.size(e) == 1) {
					resultCallback.setResult(e.get(0));
				} else {
					resultCallback.setResult(null);
				}
			}
		};
		defaultSearchComponentDialogController.searchComponentsDialog(parent, valueFilterMap, false, false, rc);
	}

	@Override
	public void searchList(IResultCallback<List<E>> resultCallback) {
		Map<String, Object> valueFilterMap = new HashMap<String, Object>();
		if (initialValueFilterMap != null) {
			valueFilterMap.putAll(initialValueFilterMap);
		}
		completeFilters(valueFilterMap);
		AbstractSearchComponentDialogController<V, E, G> defaultSearchComponentDialogController = createDefaultSearchComponentDialogController(viewFactory, initialValueFilterMap);
		defaultSearchComponentDialogController.searchComponentsDialog(parent, valueFilterMap, true, false, resultCallback);
	}
}
