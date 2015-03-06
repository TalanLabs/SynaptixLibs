package com.synaptix.widget.component.controller.context;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.synaptix.client.view.IView;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.entity.IEntity;
import com.synaptix.service.IEntityService;
import com.synaptix.widget.view.ISynaptixViewFactory;
import com.synaptix.widget.viewworker.view.AbstractLoadingViewWorker;

public abstract class AbstractEntitySearchFieldWidgetContext<V extends ISynaptixViewFactory, E extends IEntity> extends AbstractComponentExtendedSearchFieldWidgetContext<V, E, E> {

	private static final Log LOG = LogFactory.getLog(AbstractEntitySearchFieldWidgetContext.class);

	/**
	 * 
	 * @param viewFactory
	 * @param parent
	 * @param componentClass
	 * @param filterPropertyNames
	 *            use for filter and sort
	 */
	public AbstractEntitySearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, String... filterPropertyNames) {
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
	public AbstractEntitySearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, String[] filterPropertyNames, String[] suggestColumns) {
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
	public AbstractEntitySearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, Map<String, Object> initialValueFilterMap, String... filterPropertyNames) {
		this(viewFactory, parent, componentClass, initialValueFilterMap, filterPropertyNames, null);
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
	public AbstractEntitySearchFieldWidgetContext(V viewFactory, IView parent, Class<E> componentClass, Map<String, Object> initialValueFilterMap, String[] filterPropertyNames, String[] suggestColumns) {
		super(viewFactory, parent, componentClass, componentClass, initialValueFilterMap, filterPropertyNames, suggestColumns);
	}

	@Override
	protected List<E> convert(List<E> list) {
		return list;
	}

	private final IEntityService getEntityService() {
		return getServiceFactory().getService(IEntityService.class);
	}

	@Override
	public void convert(final E value, final IResultCallback<E> resultCallback) {
		if (getSuggestColumns() != null) {
			viewFactory.waitComponentViewWorker(parent, new AbstractLoadingViewWorker<E>() {
				@Override
				protected E doLoading() throws Exception {
					return getEntityService().findEntityById(componentClass, value.getId());
				}

				@Override
				public void success(E e) {
					resultCallback.setResult(e);
				}

				@Override
				public void fail(Throwable t) {
					LOG.error(t.getMessage(), t);
					resultCallback.setResult(null);
				}
			});
		} else {
			resultCallback.setResult(value);
		}
	}
}
