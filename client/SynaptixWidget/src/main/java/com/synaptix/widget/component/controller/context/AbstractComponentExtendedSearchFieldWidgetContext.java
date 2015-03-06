package com.synaptix.widget.component.controller.context;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CancellationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.client.view.IView;
import com.synaptix.client.view.IWaitWorker;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.component.IComponent;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.entity.CancellableFields;
import com.synaptix.entity.ICancellable;
import com.synaptix.service.IComponentService;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.filter.AbstractNode;
import com.synaptix.service.filter.RootNode;
import com.synaptix.service.filter.branch.AndOperator;
import com.synaptix.service.filter.branch.OrOperator;
import com.synaptix.service.filter.builder.AndOperatorBuilder;
import com.synaptix.service.filter.builder.OrOperatorBuilder;
import com.synaptix.service.filter.leaf.InPropertyValue;
import com.synaptix.service.filter.leaf.LikePropertyValue;
import com.synaptix.service.helper.SortOrderHelper;
import com.synaptix.widget.component.controller.dialog.AbstractSearchComponentDialogController;
import com.synaptix.widget.searchfield.context.AbstractSearchFieldWidgetContext;
import com.synaptix.widget.view.ISynaptixViewFactory;
import com.synaptix.widget.viewworker.view.AbstractLoadingViewWorker;

public abstract class AbstractComponentExtendedSearchFieldWidgetContext<V extends ISynaptixViewFactory, E extends IComponent, G extends E> extends AbstractSearchFieldWidgetContext<E> {

	private static final Log LOG = LogFactory.getLog(AbstractComponentExtendedSearchFieldWidgetContext.class);

	protected final V viewFactory;

	protected final IView parent;

	protected final Class<E> componentClass;

	protected final Class<G> componentExtendedClass;

	protected final String[] filterPropertyNames;

	protected final Set<String> suggestColumns;

	protected final Map<String, Object> initialValueFilterMap;

	private IWaitWorker suggestWaitWorker;

	/**
	 * 
	 * @param viewFactory
	 * @param parent
	 * @param componentClass
	 * @param componentExtendedClass
	 * @param filterPropertyNames
	 *            Use for filter and sort
	 */
	public AbstractComponentExtendedSearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, Class<G> componentExtendedClass, String... filterPropertyNames) {
		this(viewFactory, parent, componentClass, componentExtendedClass, null, filterPropertyNames, null);
	}

	/**
	 * 
	 * @param viewFactory
	 * @param parent
	 * @param componentClass
	 * @param componentExtendedClass
	 * @param filterPropertyNames
	 *            Use for filter and sort
	 * @param suggestColumns
	 */
	public AbstractComponentExtendedSearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, Class<G> componentExtendedClass, String[] filterPropertyNames,
			String[] suggestColumns) {
		this(viewFactory, parent, componentClass, componentExtendedClass, null, filterPropertyNames, null);
	}

	/**
	 * 
	 * @param viewFactory
	 * @param parent
	 * @param componentClass
	 * @param componentExtendedClass
	 * @param initialValueFilterMap
	 * @param filterPropertyNames
	 *            Use for filter and sort
	 */
	public AbstractComponentExtendedSearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, Class<G> componentExtendedClass, Map<String, Object> initialValueFilterMap,
			String... filterPropertyNames) {
		this(viewFactory, parent, componentClass, componentExtendedClass, initialValueFilterMap, filterPropertyNames, null);
	}

	/**
	 * 
	 * @param viewFactory
	 * @param parent
	 * @param componentClass
	 * @param componentExtendedClass
	 * @param initialValueFilterMap
	 * @param filterPropertyNames
	 *            Use for filter and sort
	 * @param suggestColumns
	 */
	public AbstractComponentExtendedSearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, Class<G> componentExtendedClass, Map<String, Object> initialValueFilterMap,
			String[] filterPropertyNames, String[] suggestColumns) {
		super();
		this.viewFactory = viewFactory;
		this.parent = parent;
		this.componentClass = componentClass;
		this.componentExtendedClass = componentExtendedClass;
		this.filterPropertyNames = filterPropertyNames;
		this.initialValueFilterMap = initialValueFilterMap;
		this.suggestColumns = suggestColumns != null ? new HashSet<String>(Arrays.asList(suggestColumns)) : null;
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

	@Override
	public void search(String name, final IResultCallback<E> resultCallback) {
		if (suggestWaitWorker != null && !suggestWaitWorker.isDone()) {
			suggestWaitWorker.cancel(false);
			suggestWaitWorker = null;
		}
		Map<String, Object> valueFilterMap = new HashMap<String, Object>();
		valueFilterMap.put(filterPropertyNames[0], name);
		if (initialValueFilterMap != null) {
			valueFilterMap.putAll(initialValueFilterMap);
		}
		completeFilters(valueFilterMap);
		AbstractSearchComponentDialogController<V, E, ? extends E> defaultSearchComponentDialogController = createDefaultSearchComponentDialogController(viewFactory, initialValueFilterMap);
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
		if (name != null && !name.isEmpty()) {
			defaultSearchComponentDialogController.searchUniqueComponentOrOpenDialog(parent, valueFilterMap, false, false, true, rc);
		} else {
			defaultSearchComponentDialogController.searchComponentsDialog(parent, valueFilterMap, false, false, rc);
		}
	}

	@Override
	public void searchMany(String name, final IResultCallback<List<E>> resultCallback) {
		if (suggestWaitWorker != null && !suggestWaitWorker.isDone()) {
			suggestWaitWorker.cancel(false);
			suggestWaitWorker = null;
		}
		Map<String, Object> valueFilterMap = new HashMap<String, Object>();
		valueFilterMap.put(filterPropertyNames[0], name);
		if (initialValueFilterMap != null) {
			valueFilterMap.putAll(initialValueFilterMap);
		}
		completeFilters(valueFilterMap);
		AbstractSearchComponentDialogController<V, E, ? extends E> defaultSearchComponentDialogController = createDefaultSearchComponentDialogController(viewFactory, initialValueFilterMap);
		IResultCallback<List<E>> rc = new IResultCallback<List<E>>() {
			@Override
			public void setResult(List<E> e) {
				if (CollectionHelper.size(e) > 0) {
					resultCallback.setResult(e);
				} else {
					resultCallback.setResult(null);
				}
			}
		};
		if (name != null && !name.isEmpty()) {
			defaultSearchComponentDialogController.searchUniqueComponentOrOpenDialog(parent, valueFilterMap, true, false, true, rc);
		} else {
			defaultSearchComponentDialogController.searchComponentsDialog(parent, valueFilterMap, true, false, rc);
		}
	}

	protected abstract AbstractSearchComponentDialogController<V, E, ? extends E> createDefaultSearchComponentDialogController(V viewFactory, Map<String, Object> initialValueFilterMap);

	protected abstract IServiceFactory getServiceFactory();

	private final IComponentService getComponentService() {
		return getServiceFactory().getService(IComponentService.class);
	}

	protected void createFilterNode(OrOperatorBuilder orOperatorBuilder, String filterPropertyName, String name) {
		orOperatorBuilder.addLikePropertyValue(LikePropertyValue.Type.like_right, filterPropertyName, name);
	}

	protected AbstractNode createFilterNodes(String name) {
		OrOperator orOperator = null;
		if (filterPropertyNames != null && filterPropertyNames.length > 0) {
			OrOperatorBuilder orOperatorBuilder = new OrOperatorBuilder();
			for (String filterPropertyName : filterPropertyNames) {
				createFilterNode(orOperatorBuilder, filterPropertyName, name);
			}
			orOperator = orOperatorBuilder.build();
		}
		return orOperator;
	}

	/**
	 * Call suggest service
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected List<E> suggestService(String name) throws Exception {
		SortOrderHelper.Builder sortOrderBuilder = new SortOrderHelper.Builder();

		AbstractNode filterNode = createFilterNodes(name);

		if (filterPropertyNames != null && filterPropertyNames.length > 0) {
			for (String filterPropertyName : filterPropertyNames) {
				sortOrderBuilder.addProperty(filterPropertyName, true);
			}
		}

		Map<String, Object> andValueFilterMap = new HashMap<String, Object>();
		if (initialValueFilterMap != null && !initialValueFilterMap.isEmpty()) {
			andValueFilterMap.putAll(initialValueFilterMap);
		}
		completeFilters(andValueFilterMap);

		AndOperator andOperator = null;
		if (andValueFilterMap != null && !andValueFilterMap.isEmpty()) {
			AndOperatorBuilder andOperatorBuilder = new AndOperatorBuilder();
			for (Entry<String, Object> entry : andValueFilterMap.entrySet()) {
				if (Collection.class.isAssignableFrom(entry.getValue().getClass())) {
					andOperatorBuilder.addInPropertyValue(InPropertyValue.Type.in, entry.getKey(), ((Collection<?>) entry.getValue()).toArray());
				} else {
					andOperatorBuilder.addEqualsPropertyValue(entry.getKey(), entry.getValue());
				}
			}
			andOperator = andOperatorBuilder.build();
		}

		RootNode rootNode = null;
		if (filterNode != null && andOperator != null) {
			rootNode = new RootNode(new AndOperator(andOperator, filterNode));
		} else if (filterNode != null) {
			rootNode = new RootNode(filterNode);
		} else if (andOperator != null) {
			rootNode = new RootNode(andOperator);
		}

		return convert(getComponentService().selectSuggest(componentExtendedClass, rootNode, sortOrderBuilder.build(), getSuggestColumns()));
	}

	protected Set<String> getSuggestColumns() {
		return suggestColumns;
	}

	protected List<E> convert(List<G> list) {
		return ComponentHelper.convertComponentExtendedToComponent(componentClass, list);
	}

	@Override
	public void suggest(final String name, final IResultCallback<List<E>> resultCallback) {
		if (suggestWaitWorker != null && !suggestWaitWorker.isDone()) {
			suggestWaitWorker.cancel(false);
			suggestWaitWorker = null;
		}
		suggestWaitWorker = viewFactory.waitComponentViewWorker(parent, new AbstractLoadingViewWorker<List<E>>() {
			@Override
			protected List<E> doLoading() throws Exception {
				return suggestService(name);
			}

			@Override
			public void success(List<E> e) {
				resultCallback.setResult(e);
			}

			@Override
			public void fail(Throwable t) {
				if (!(t instanceof CancellationException)) {
					LOG.error(t.getMessage(), t);
					resultCallback.setResult(null);
				}
			}
		});
	}
}
