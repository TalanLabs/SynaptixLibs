package com.synaptix.widget.component.view;

import java.util.Set;

import com.synaptix.component.IComponent;

public interface ITablePageComponentsView<E extends IComponent> extends ITableComponentsView<E> {

	/**
	 * 
	 * @param currentPage
	 * @param pagesNumber
	 * @param count
	 */
	public void setPaginationView(int currentPage, boolean first, boolean previous, boolean next, boolean last);

	public void setCountLine(Integer count);

	/**
	 * Get the column list
	 * 
	 * @return
	 */
	public Set<String> getColumns();

}
