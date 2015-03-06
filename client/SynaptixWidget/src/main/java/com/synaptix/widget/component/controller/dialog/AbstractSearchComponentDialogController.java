package com.synaptix.widget.component.controller.dialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;

import com.synaptix.client.view.IView;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.component.IComponent;
import com.synaptix.service.IEntityService;
import com.synaptix.service.IPaginationService;
import com.synaptix.widget.component.controller.context.AbstractSearchComponentsContext;
import com.synaptix.widget.component.view.ISearchComponentsDialogView;
import com.synaptix.widget.component.view.ISearchComponentsDialogViewDescriptor;
import com.synaptix.widget.component.view.ISearchTablePageComponentsView;
import com.synaptix.widget.view.ISynaptixViewFactory;
import com.synaptix.widget.view.IViewDescriptor;
import com.synaptix.widget.viewworker.view.AbstractLoadingViewWorker;

/**
 * Create a search component dialog, table and filter for component
 * 
 * @param <V>
 *            View Factory
 * @param <E>
 *            Return component
 * @param <G>
 *            Pagination entity which
 */
public abstract class AbstractSearchComponentDialogController<V extends ISynaptixViewFactory, E extends IComponent, G extends IComponent> extends AbstractSearchComponentsContext<V, G> {

	protected final String title;

	protected final Class<E> editComponentClass;

	private String subtitle;

	/**
	 * Constructs a <code>DefaultSearchComponentController</code> with multiple selection enabled and read only disabled.
	 * 
	 * @param viewFactory
	 * @param editComponentClass
	 * @param paginationComponentClass
	 * @param title
	 */
	public AbstractSearchComponentDialogController(V viewFactory, Class<E> editComponentClass, Class<G> paginationComponentClass, String title) {
		this(viewFactory, editComponentClass, paginationComponentClass, null, title);
	}

	/**
	 * 
	 * @param viewFactory
	 * @param editComponentClass
	 * @param paginationComponentClass
	 * @param paginationServiceClass
	 * @param title
	 */
	public AbstractSearchComponentDialogController(V viewFactory, Class<E> editComponentClass, Class<G> paginationComponentClass, Class<? extends IPaginationService<G>> paginationServiceClass,
			String title) {
		super(viewFactory, paginationComponentClass, paginationServiceClass);

		this.title = title;
		this.editComponentClass = editComponentClass;
	}

	@Override
	protected final ISearchTablePageComponentsView<G> createSearchComponentsView() {
		return createSearchComponentsDialogView();
	}

	/**
	 * Create a search components dialog view. NOT CALL use getSearchComponentsDialogView()
	 * 
	 * @return
	 */
	protected ISearchComponentsDialogView<G> createSearchComponentsDialogView() {
		return getViewFactory().newSearchComponentsDialogView(componentClass, getViewDescriptor());
	}

	/**
	 * Get search components dialog view
	 * 
	 * @return
	 */
	protected final ISearchComponentsDialogView<G> getSearchComponentsDialogView() {
		return (ISearchComponentsDialogView<G>) getSearchComponentsView();
	}

	/**
	 * Create a search components dialog view descriptor. NOT CALL use getSearchComponentsDialogViewDescriptor()
	 * 
	 * @return
	 */
	protected abstract ISearchComponentsDialogViewDescriptor<G> createSearchComponentsDialogViewDescriptor();

	@Override
	protected final IViewDescriptor<G> createViewDescriptor() {
		return createSearchComponentsDialogViewDescriptor();
	}

	/**
	 * Get search components dialog view descriptor
	 * 
	 * @return
	 */
	protected final ISearchComponentsDialogViewDescriptor<G> getSearchComponentsDialogViewDescriptor() {
		return (ISearchComponentsDialogViewDescriptor<G>) getViewDescriptor();
	}

	/**
	 * Get a subtitle
	 * 
	 * @return
	 */
	public String getSubtitle() {
		return subtitle;
	}

	/**
	 * Set a subtitle
	 * 
	 * @param subtitle
	 */
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	/**
	 * Get entity service
	 * 
	 * @return
	 */
	protected final IEntityService getEntityService() {
		return getServiceFactory().getService(IEntityService.class);
	}

	/**
	 * Show search dialog, Single selection
	 * 
	 * @param parentView
	 * @param result
	 */
	public void searchComponentsDialog(IView parentView, IResultCallback<List<E>> result) {
		searchComponentsDialog(parentView, null, false, false, result);
	}

	/**
	 * Show search dialog, Single selection
	 * 
	 * @param parentView
	 * @param filters
	 * @param result
	 */
	public void searchComponentsDialog(IView parentView, Map<String, Object> filters, IResultCallback<List<E>> result) {
		searchComponentsDialog(parentView, filters, false, false, result);
	}

	/**
	 * Show Search dialog
	 * 
	 * @param parentView
	 * @param filters
	 * @param multipleSelection
	 * @param readOnly
	 * @param result
	 */
	public void searchComponentsDialog(IView parentView, Map<String, Object> valueFilterMap, boolean multipleSelection, boolean readOnly, IResultCallback<List<E>> resultCallback) {
		searchComponentsDialog(parentView, valueFilterMap, multipleSelection, readOnly, false, resultCallback);
	}

	/**
	 * Show Search dialog
	 * 
	 * @param parentView
	 * @param valueFilterMap
	 * @param multipleSelection
	 * @param readOnly
	 * @param startSearch
	 * @param resultCallback
	 */
	public void searchComponentsDialog(IView parentView, Map<String, Object> valueFilterMap, boolean multipleSelection, boolean readOnly, boolean startSearch, IResultCallback<List<E>> resultCallback) {
		if (getSearchComponentsDialogView().showDialog(parentView, valueFilterMap, title, subtitle, multipleSelection, readOnly, startSearch) == ISearchComponentsDialogView.ACCEPT_OPTION) {
			loadFullComponents(getSearchComponentsDialogView().getSelectedComponents(), resultCallback);
		} else {
			resultCallback.setResult(null);
		}
	}

	/**
	 * Search if exist a unique result
	 * 
	 * @param parentView
	 * @param valueFilterMap
	 * @param resultCallback
	 */
	public void searchUniqueComponent(final IView parentView, final Map<String, Object> valueFilterMap, final IResultCallback<E> resultCallback, final Set<String> columns) {
		completeFilters(valueFilterMap);
		getViewFactory().waitComponentViewWorker(parentView, new AbstractLoadingViewWorker<G>() {
			@Override
			protected G doLoading() throws Exception {
				G res = null;
				int count = countPagination(valueFilterMap);
				if (count == 1) {
					res = selectPagination(valueFilterMap, 0, 1, null, columns).get(0);
				}
				return res;
			}

			@Override
			public void success(G e) {
				loadFullComponent(e, resultCallback);
			}

			@Override
			public void fail(Throwable t) {
				if (!(t instanceof CancellationException)) {
					getViewFactory().showErrorMessageDialog(parentView, t);
				}
			}
		});
	}

	/**
	 * Search a unique value and if not exists open dialog
	 * 
	 * @param parentView
	 * @param valueFilterMap
	 * @param multipleSelection
	 * @param readOnly
	 * @param resultCallback
	 */
	public void searchUniqueComponentOrOpenDialog(final IView parentView, final Map<String, Object> valueFilterMap, final boolean multipleSelection, final boolean readOnly, final boolean startSearch,
			final IResultCallback<List<E>> resultCallback) {
		if (!isEmptyValueFilterMap(valueFilterMap) && !multipleSelection && !readOnly) {
			searchUniqueComponent(parentView, valueFilterMap, new IResultCallback<E>() {
				@Override
				public void setResult(E e) {
					if (e != null) {
						List<E> ls = new ArrayList<E>();
						ls.add(e);
						resultCallback.setResult(ls);
					} else {
						searchComponentsDialog(parentView, valueFilterMap, multipleSelection, readOnly, startSearch, resultCallback);
					}
				}
			}, getSearchComponentsView().getColumns());
		} else {
			searchComponentsDialog(parentView, null, multipleSelection, readOnly, resultCallback);
		}
	}

	private boolean isEmptyValueFilterMap(Map<String, Object> valueFilterMap) {
		if (valueFilterMap == null || valueFilterMap.isEmpty()) {
			return true;
		}
		boolean res = true;
		Iterator<Object> it = valueFilterMap.values().iterator();
		while (it.hasNext() && res) {
			Object value = it.next();
			res = value == null;
		}
		return res;
	}

	protected abstract E findFullComponent(G paginationEntity);

	protected final void loadFullComponent(final G paginationEntity, final IResultCallback<E> resultCallback) {
		if (paginationEntity == null) {
			resultCallback.setResult(null);
		} else {
			getViewFactory().waitFullComponentViewWorker(getSearchComponentsView(), new AbstractLoadingViewWorker<E>() {

				@Override
				protected E doLoading() throws Exception {
					return findFullComponent(paginationEntity);
				}

				@Override
				public void success(E e) {
					resultCallback.setResult(e);
				}

				@Override
				public void fail(Throwable t) {
					getViewFactory().showErrorMessageDialog(getSearchComponentsView(), t);
				}
			});
		}
	}

	protected final void loadFullComponents(final List<G> paginationEntities, final IResultCallback<List<E>> resultCallback) {
		if (paginationEntities == null || paginationEntities.isEmpty()) {
			resultCallback.setResult(null);
		}
		getViewFactory().waitFullComponentViewWorker(getSearchComponentsView(), new AbstractLoadingViewWorker<List<E>>() {

			@Override
			protected List<E> doLoading() throws Exception {
				List<E> res = new ArrayList<E>();
				if (!paginationEntities.isEmpty()) {
					for (G paginationEntity : paginationEntities) {
						res.add(findFullComponent(paginationEntity));
					}
				}
				return res;
			}

			@Override
			public void success(List<E> e) {
				resultCallback.setResult(e);
			}

			@Override
			public void fail(Throwable t) {
				getViewFactory().showErrorMessageDialog(getSearchComponentsView(), t);
			}
		});
	}
}
