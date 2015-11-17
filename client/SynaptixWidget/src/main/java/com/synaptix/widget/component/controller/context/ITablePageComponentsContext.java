package com.synaptix.widget.component.controller.context;

import java.util.List;

import com.synaptix.client.view.IView;
import com.synaptix.service.model.ISortOrder;

public interface ITablePageComponentsContext {

	/**
	 * Sort page according to specified orders
	 */
	public void sortPage(List<ISortOrder> orderList, boolean fireRefresh);

	/**
	 * Go to first page
	 */
	public void firstPage();

	/**
	 * Go to last page
	 */
	public void lastPage();

	/**
	 * Go to previsous page
	 */
	public void previousPage();

	/**
	 * Go to next page
	 */
	public void nextPage();

	/**
	 * Change the size of the page
	 */
	public void selectSizePage(IView parent);

	/**
	 * Returns the current Page Size
	 *
	 * @return
	 */
	public int getCurrentPageSize();

	/**
	 * Export all lines or current page
	 *
	 * @param allLines
	 */
	public void exportExcel(boolean allLines);

	/**
	 * Count number of lines
	 */
	public void countLines();

}