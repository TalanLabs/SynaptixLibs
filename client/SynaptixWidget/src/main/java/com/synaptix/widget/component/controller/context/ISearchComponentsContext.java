package com.synaptix.widget.component.controller.context;

import java.util.Map;

public interface ISearchComponentsContext extends ITablePageComponentsContext {

	/**
	 * Search components with value filter map
	 * 
	 * @param valueFilterMap
	 */
	public void searchComponents(Map<String, Object> valueFilterMap);

	/**
	 * Load number of components to create pagination
	 */
	public void loadPagination();

	/**
	 * Updates pagination label and refreshes data of current page
	 */
	public void updatePagination();

	/**
	 * Refreshes pagination and components without losing current page, or fetches the last page if the current page is not available anymore
	 */
	public void refreshPagination();

	/**
	 * Set search axis
	 * 
	 * @param searchAxis
	 */
	public void setSearchAxis(String searchAxis);

}