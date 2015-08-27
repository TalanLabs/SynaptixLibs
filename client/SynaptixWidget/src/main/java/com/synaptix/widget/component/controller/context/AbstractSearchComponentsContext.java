package com.synaptix.widget.component.controller.context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;

import com.synaptix.client.view.IView;
import com.synaptix.client.view.IWaitWorker;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.common.util.IResultCallback;
import com.synaptix.component.IComponent;
import com.synaptix.entity.CancellableFields;
import com.synaptix.entity.ICancellable;
import com.synaptix.prefs.SyPreferences;
import com.synaptix.service.IComponentService;
import com.synaptix.service.IPaginationService;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.helper.SortOrderHelper;
import com.synaptix.service.model.ISortOrder;
import com.synaptix.swing.utils.Manager;
import com.synaptix.swing.utils.SyDesktop;
import com.synaptix.widget.component.controller.dialog.SelectSizePageDialogController;
import com.synaptix.widget.component.util.SearchListener;
import com.synaptix.widget.component.view.ISearchTablePageComponentsView;
import com.synaptix.widget.filefilter.view.ExcelFileFilter;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.ISynaptixViewFactory;
import com.synaptix.widget.view.IViewDescriptor;
import com.synaptix.widget.viewworker.view.AbstractLoadingViewWorker;

public abstract class AbstractSearchComponentsContext<V extends ISynaptixViewFactory, E extends IComponent> implements ISearchComponentsContext {

	private final V viewFactory;

	protected final Class<E> componentClass;

	private final Class<? extends IPaginationService<E>> paginationServiceClass;

	private int sizePage;

	private int currentPage;

	private int pagesNumber;

	private int count;

	private int currentResultCount;

	private boolean knowCount;

	private List<ISortOrder> orderList;

	private Map<String, Object> valueFilterMapBeforeChange = new HashMap<String, Object>();

	private Map<String, Object> valueFilterMap = new HashMap<String, Object>();

	private IWaitWorker loadCountPaginationWorker;

	private IWaitWorker loadComponentsWorker;

	private List<SearchListener> searchListenerList;

	private ISearchTablePageComponentsView<E> searchComponentsView;

	private IViewDescriptor<E> viewDescriptor;

	private int maxExcelExportLine;

	private String searchAxis;

	public AbstractSearchComponentsContext(V viewFactory, Class<E> componentClass) {
		this(viewFactory, componentClass, 100);
	}

	public AbstractSearchComponentsContext(V viewFactory, Class<E> componentClass, Class<? extends IPaginationService<E>> paginationServiceClass) {
		this(viewFactory, componentClass, paginationServiceClass, 100);
	}

	public AbstractSearchComponentsContext(V viewFactory, Class<E> componentClass, int defaultSizePage) {
		this(viewFactory, componentClass, null, defaultSizePage);
	}

	public AbstractSearchComponentsContext(V viewFactory, Class<E> componentClass, Class<? extends IPaginationService<E>> paginationServiceClass, int defaultSizePage) {
		super();

		this.viewFactory = viewFactory;
		this.componentClass = componentClass;
		this.sizePage = defaultSizePage;
		this.paginationServiceClass = paginationServiceClass;
		this.orderList = null;

		this.maxExcelExportLine = 10000;

		this.knowCount = false;
		this.currentPage = 1;
		this.count = 0;
		this.pagesNumber = -1;
		this.currentResultCount = -1;

		loadUserFavorites();
	}

	public final V getViewFactory() {
		return viewFactory;
	}

	private void saveUserFavorites() {
		SyPreferences prefs = SyPreferences.getPreferences();
		String name = AbstractSearchComponentsContext.class.getName() + "_" + componentClass.getSimpleName() + "_favorites_user";

		prefs.putInt(name + "_nb", getSizePage());
	}

	private void loadUserFavorites() {
		SyPreferences prefs = SyPreferences.getPreferences();
		String name = AbstractSearchComponentsContext.class.getName() + "_" + componentClass.getSimpleName() + "_favorites_user";

		int prefSizePage = prefs.getInt(name + "_nb", 0);
		if (prefSizePage > 0) {
			sizePage = prefSizePage;
		}
	}

	/**
	 * Get a current viewFactory
	 *
	 * @return
	 */
	public V getSearchComponentsViewFactory() {
		return viewFactory;
	}

	/**
	 * Get view descriptor
	 *
	 * @return
	 */
	public final IViewDescriptor<E> getViewDescriptor() {
		if (viewDescriptor == null) {
			viewDescriptor = createViewDescriptor();
			viewDescriptor.setSearchComponentsContext(this);
			viewDescriptor.create();
		}
		return viewDescriptor;
	}

	/**
	 * Get a search components view
	 *
	 * @return
	 */
	protected final ISearchTablePageComponentsView<E> getSearchComponentsView() {
		if (searchComponentsView == null) {
			searchComponentsView = createSearchComponentsView();
			searchComponentsView.setSearchComponentsContext(this);
			searchComponentsView.create();
		}
		return searchComponentsView;
	}

	/**
	 * Create a search components view. NOT CALL, use getSearchComponentsView()
	 *
	 * @return
	 */
	protected ISearchTablePageComponentsView<E> createSearchComponentsView() {
		return getViewFactory().newSearchComponentsView(componentClass, getViewDescriptor());
	}

	/**
	 * Create a view descriptor. NOT CALL, use getViewDescriptor()
	 *
	 * @param searchComponentsContext
	 * @return
	 */
	protected abstract IViewDescriptor<E> createViewDescriptor();

	/**
	 * Get a size page
	 *
	 * @return
	 */
	public final int getSizePage() {
		return sizePage;
	}

	/**
	 * Change size page
	 *
	 * @param sizePage
	 */
	protected final void setSizePage(int sizePage) {
		this.sizePage = sizePage;
		saveUserFavorites();
	}

	public final IView getView() {
		return getSearchComponentsView();
	}

	protected abstract IServiceFactory getServiceFactory();

	protected final IComponentService getComponentService() {
		return getServiceFactory().getService(IComponentService.class);
	}

	protected final IPaginationService<E> getPaginationService() {
		return getServiceFactory().getService(paginationServiceClass);
	}

	/**
	 * Searches for a new list of components given filters
	 */
	@Override
	public void searchComponents(Map<String, Object> valueFilterMap) {
		this.valueFilterMap = valueFilterMap;

		if (this.valueFilterMap == null) {
			this.valueFilterMap = new HashMap<String, Object>();
		}
		loadPagination();
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

	/**
	 * Count pagination, use pagination service or default
	 *
	 * @param valueFilterMap
	 * @return
	 */
	protected final int countPagination(Map<String, Object> valueFilterMap) {
		if (paginationServiceClass != null) {
			return getPaginationService().countPagination(valueFilterMap);
		} else {
			return countDefaultPagination(valueFilterMap);
		}
	}

	/**
	 * Overwrite for change default service
	 *
	 * @param valueFilterMap
	 * @return
	 */
	protected int countDefaultPagination(Map<String, Object> valueFilterMap) {
		return getComponentService().countPaginationOld(componentClass, valueFilterMap);
	}

	/**
	 * Select pagination, use pagination service or default
	 *
	 * @param valueFilterMap
	 * @param from
	 * @param to
	 * @param orders
	 * @param columnList
	 * @return
	 */
	protected final List<E> selectPagination(Map<String, Object> valueFilterMap, int from, int to, List<ISortOrder> orders, Set<String> columns) {
		if (paginationServiceClass != null) {
			return getPaginationService().selectPagination(valueFilterMap, from, to, orders, columns);
		} else {
			return selectDefaultPagination(valueFilterMap, from, to, orders, columns);
		}
	}

	/**
	 * Select pagination for excel<br/>
	 * By default, uses the standard pagination<br/>
	 * This method is called within a loading view worker
	 *
	 * @param valueFilterMap
	 * @param from
	 * @param to
	 * @param orders
	 * @param columns
	 * @return
	 */
	protected List<E> selectPaginationForExcel(Map<String, Object> valueFilterMap, int from, int to, List<ISortOrder> orders, Set<String> columns) {
		return selectPagination(valueFilterMap, from, to, orderList, getColumns());
	}

	/**
	 * Overwrite for change default service
	 *
	 * @param valueFilterMap
	 * @param from
	 * @param to
	 * @param orders
	 * @param columnList
	 * @return
	 */
	protected List<E> selectDefaultPagination(Map<String, Object> valueFilterMap, int from, int to, List<ISortOrder> orders, Set<String> columns) {
		return getComponentService().selectPaginationOld(componentClass, valueFilterMap, from, to, orders, columns);
	}

	@Override
	public void loadPagination() {
		cancelWork();
		reset();

		valueFilterMapBeforeChange = new HashMap<String, Object>();
		valueFilterMapBeforeChange.putAll(valueFilterMap);
		completeFilters(valueFilterMap);

		loadComponents(true);
	}

	@Override
	public void updatePagination() {
		updatePagination(false);
	}

	public void updatePagination(boolean fire) {
		cancelWork();
		loadComponents(fire);
	}

	@Override
	public void refreshPagination() {
		loadPagination();
	}

	@Override
	public final void countLines() {
		countLines(new IResultCallback<Integer>() {
			@Override
			public void setResult(Integer e) {
				setPagination(e != null ? e : 0);
				getSearchComponentsView().setCountLine(e);

				boolean fp = currentPage > 1;
				boolean nl = knowCount ? currentPage < pagesNumber : currentResultCount == sizePage;
				getSearchComponentsView().setPaginationView(currentPage, fp, fp, nl, nl);
			}
		});
	}

	private final void countLines(final IResultCallback<Integer> resultCallback) {
		loadCountPaginationWorker = viewFactory.waitFullComponentViewWorker(getSearchComponentsView(), new AbstractLoadingViewWorker<Integer>() {
			@Override
			protected Integer doLoading() throws Exception {
				publish(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().counting());
				return countPagination(valueFilterMap);
			}

			@Override
			public void success(Integer result) {
				resultCallback.setResult(result);
			}

			@Override
			public void fail(Throwable t) {
				if (!(t instanceof CancellationException)) {
					viewFactory.showErrorMessageDialog(getSearchComponentsView(), t);
				}
				resultCallback.setResult(null);
			}
		});
	}

	/**
	 * Adds a search listener to be notified when a new search was performed
	 */
	protected final void addSearchListener(SearchListener searchListener) {
		if (searchListenerList == null) {
			searchListenerList = new ArrayList<SearchListener>();
		}
		searchListenerList.add(searchListener);
	}

	/**
	 * Removes a search listener
	 */
	protected final boolean removeSearchListener(SearchListener searchListener) {
		if (searchListenerList != null) {
			return searchListenerList.remove(searchListener);
		}
		return false;
	}

	/**
	 * Fire a listener
	 *
	 * @param unmodifiableMap
	 */
	private void fireSearchListener(Map<String, Object> valueFilterMap) {
		if (CollectionHelper.isNotEmpty(searchListenerList)) {
			Iterator<SearchListener> ite = searchListenerList.iterator();
			while (ite.hasNext()) {
				if (ite.next().searchPerformed(valueFilterMap)) {
					ite.remove();
				}
			}
		}
	}

	/**
	 * Load components for "from" to "to", ordered by "order"
	 *
	 * @param from
	 * @param to
	 * @param count
	 * @param order
	 *            e.g. "TEXT ASC, DESCRIPTION DESC"
	 */
	private void loadComponents(final boolean fire) {
		loadComponentsWorker = viewFactory.waitFullComponentViewWorker(getSearchComponentsView(), new AbstractLoadingViewWorker<List<E>>() {

			@Override
			protected List<E> doLoading() throws Exception {
				int from = (currentPage - 1) * sizePage + 1;
				int to = from + sizePage - 1;
				publish(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().fetchingPageX(currentPage));
				return selectPagination(valueFilterMap, from, to, orderList, getColumns());
			}

			@Override
			public void success(List<E> e) {
				getSearchComponentsView().setComponents(e);
				setComponentsResult(e);

				boolean resultCountChanged = e != null ? e.size() != currentResultCount : true;
				currentResultCount = e != null ? e.size() : 0;
				if (!knowCount) {
					if (currentResultCount < sizePage) {
						setPagination((currentPage - 1) * sizePage + currentResultCount);
						getSearchComponentsView().setCountLine(count);
					} else {
						getSearchComponentsView().setCountLine(-1);
					}
				}

				if ((resultCountChanged) || (fire)) {
					boolean fp = currentPage > 1;
					boolean nl = knowCount ? currentPage < pagesNumber : currentResultCount == sizePage;
					getSearchComponentsView().setPaginationView(currentPage, fp, fp, nl, nl);
				}
				if (fire) {
					fireSearchListener(valueFilterMapBeforeChange != null ? Collections.unmodifiableMap(valueFilterMapBeforeChange) : null);
				}
			}

			@Override
			public void fail(Throwable t) {
				if (!(t instanceof CancellationException)) {
					viewFactory.showErrorMessageDialog(getSearchComponentsView(), t);

					reset();
				}
			}
		});
	}

	/**
	 * Set load components result
	 *
	 * @param components
	 */
	protected void setComponentsResult(List<E> components) {
	}

	/**
	 * Sets the pagination (such as count and pageNumber) according to the number of elements
	 *
	 * @param count
	 */
	private void setPagination(int count) {
		this.knowCount = true;
		this.pagesNumber = calcPageNumber(count);
		this.count = count;
	}

	/**
	 * Computes the number of pages needed for a certain amount of results
	 *
	 * @param count
	 */
	private int calcPageNumber(int count) {
		int number = count / sizePage;
		if (count % sizePage > 0) {
			number++;
		}
		return number;
	}

	@Override
	public void sortPage(List<ISortOrder> orderList) {
		if (!SortOrderHelper.equalsSortOrder(this.orderList, orderList)) {
			// we refresh only if the order has changed
			this.orderList = orderList;
			updatePagination();
		}
	}

	/**
	 * Goes to first page and refreshes data
	 */
	@Override
	public void firstPage() {
		if (currentPage > 1) {
			currentPage = 1;

			updatePagination(true);
		}
	}

	/**
	 * Goes to last page and refreshes data
	 */
	@Override
	public void lastPage() {
		if (knowCount) {
			if (currentPage < pagesNumber) {
				currentPage = pagesNumber;

				updatePagination(true);
			}
		} else {
			countLines(new IResultCallback<Integer>() {
				@Override
				public void setResult(Integer e) {
					setPagination(e != null ? e : 0);
					getSearchComponentsView().setCountLine(e);
					if (e != null) {
						lastPage();
					}
				}
			});
		}
	}

	/**
	 * Goes to previous page and refreshes data
	 */
	@Override
	public void previousPage() {
		if (currentPage > 1) {
			currentPage--;

			updatePagination(true);
		}
	}

	/**
	 * Goes to next page and refreshes data
	 */
	@Override
	public void nextPage() {
		if (!knowCount || currentPage < pagesNumber) {
			currentPage++;

			updatePagination(true);
		}
	}

	public final int getCurrentPage() {
		return currentPage;
	}

	/**
	 * Cancel loading
	 *
	 * @return true if stopped (or was working)
	 */
	public final void cancelWork() {
		if (loadCountPaginationWorker != null && !loadCountPaginationWorker.isDone()) {
			loadCountPaginationWorker.cancel(false);
			loadCountPaginationWorker = null;
		}
		if (loadComponentsWorker != null && !loadComponentsWorker.isDone()) {
			loadComponentsWorker.cancel(false);
			loadComponentsWorker = null;
		}
	}

	/**
	 * Reset the statistics + table
	 */
	public final void reset() {
		this.knowCount = false;
		this.currentPage = 1;
		this.count = 0;
		this.pagesNumber = -1;
		this.currentResultCount = -1;

		getSearchComponentsView().setPaginationView(0, false, false, false, false);
		getSearchComponentsView().setCountLine(null);
		getSearchComponentsView().setComponents(null);
		setComponentsResult(null);
	}

	/**
	 * Opens a dialog to select the size of the page from getMinSizePage() to getMaxSizePage()
	 */
	@Override
	public void selectSizePage(IView parent) {
		SelectSizePageDialogController<V> selectSizePageDialogController = new SelectSizePageDialogController<V>(viewFactory, parent, getMinSizePage(), getMaxSizePage());
		IResultCallback<Integer> resultCallback = new IResultCallback<Integer>() {

			@Override
			public void setResult(Integer e) {
				if ((e != null) && (e != getSizePage())) {
					setSizePage(e);
					loadPagination();
				}
			}
		};

		selectSizePageDialogController.selectSizePage(parent, resultCallback, getSizePage());
	}

	/**
	 * The minimum size selectable from the the select page size dialog. By default: 100
	 *
	 * @return
	 */
	protected int getMinSizePage() {
		return 20;
	}

	/**
	 * The maximum size selectable from the the select page size dialog. By default: 1000
	 *
	 * @return
	 */
	protected int getMaxSizePage() {
		return 1000;
	}

	/**
	 * Returns the current page size
	 */
	@Override
	public int getCurrentPageSize() {
		return getSizePage();
	}

	/**
	 * Get the column list
	 *
	 * @return
	 */
	protected Set<String> getColumns() {
		return getSearchComponentsView().getColumns();
	}

	/**
	 * Set max excel export line
	 *
	 * @param maxExcelExportLine
	 */
	protected final void setMaxExcelExportLine(int maxExcelExportLine) {
		this.maxExcelExportLine = maxExcelExportLine;
	}

	/**
	 * Get max excel export line
	 *
	 * @return
	 */
	protected final int getMaxExcelExportLine() {
		return maxExcelExportLine;
	}

	@Override
	public void exportExcel(boolean allLines) {
		if (allLines) {
			if (knowCount) {
				int f = 1;
				int t = count;
				_exportExcel(f, t);
			} else {
				countLines(new IResultCallback<Integer>() {
					@Override
					public void setResult(Integer e) {
						setPagination(e != null ? e : 0);
						getSearchComponentsView().setCountLine(e);
						if (e != null) {
							exportExcel(true);
						}
					}
				});
			}
		} else {
			int f = (currentPage - 1) * sizePage + 1;
			int t = f + sizePage - 1;
			_exportExcel(f, t);
		}

	}

	private void _exportExcel(final int from, final int to) {
		final int size = to - from + 1;
		if (size > getMaxExcelExportLine()) {
			viewFactory.showErrorMessageDialog(getView(), StaticWidgetHelper.getSynaptixWidgetConstantsBundle().exportExcel(), StaticWidgetHelper.getSynaptixWidgetConstantsBundle()
					.maxExcelExportLine(getMaxExcelExportLine()));
		} else {
			File f = null;
			if (Manager.isAutoSaveExportExcel()) {
				try {
					f = File.createTempFile("Export", ".xlsx");
					f.deleteOnExit();
				} catch (IOException e) {
					viewFactory.showErrorMessageDialog(getSearchComponentsView(), e);
				}
			} else {
				f = viewFactory.chooseSaveFile(getView(), null, new ExcelFileFilter());
			}

			final File file = f;
			if (file != null) {
				viewFactory.waitFullComponentViewWorker(getView(), new AbstractLoadingViewWorker<Boolean>() {

					@Override
					protected Boolean doLoading() throws Exception {
						publish(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().fetchingXLines(size));
						List<E> components = selectPaginationForExcel(valueFilterMap, from, to, orderList, getColumns());
						publish(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().createExcelFile());
						searchComponentsView.exportExcel(file, components);
						return true;
					}

					@Override
					public void fail(Throwable t) {
						viewFactory.showErrorMessageDialog(getSearchComponentsView(), t);
					}

					@Override
					public void success(Boolean e) {
						try {
							SyDesktop.open(file);
						} catch (Exception e1) {
							viewFactory.showErrorMessageDialog(getSearchComponentsView(), e1);
						}
					}
				});
			}
		}
	}

	@Override
	public final void setSearchAxis(String searchAxis) {
		this.searchAxis = searchAxis;
	}

	public final String getSearchAxis() {
		return searchAxis;
	}
}
