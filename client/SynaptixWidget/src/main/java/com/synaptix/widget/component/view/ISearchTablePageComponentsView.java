package com.synaptix.widget.component.view;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.synaptix.client.view.IView;
import com.synaptix.component.IComponent;
import com.synaptix.widget.component.controller.context.ISearchComponentsContext;

public interface ISearchTablePageComponentsView<E extends IComponent> extends ITablePageComponentsView<E>, IView {

	/**
	 * Set search components context
	 * 
	 * @param searchComponentsContext
	 */
	public void setSearchComponentsContext(ISearchComponentsContext searchComponentsContext);

	/**
	 * Create view, Called after setSearchComponentsContext
	 */
	public void create();

	/**
	 * Export all components in excel file
	 * 
	 * @param file
	 * @param components
	 * @throws Exception
	 */
	public void exportExcel(File file, List<E> components) throws Exception;

	/**
	 * Set new value filters (works with default filters and perimeters)
	 * 
	 * @param map
	 */
	public void setValueFilters(Map<String, Object> map);

}
