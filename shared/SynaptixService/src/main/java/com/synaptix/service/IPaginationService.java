package com.synaptix.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.synaptix.component.IComponent;
import com.synaptix.service.model.ISortOrder;

public interface IPaginationService<E extends IComponent> {

	/**
	 * Get a number of line
	 * 
	 * @param valueFilterMap
	 * @return
	 * @throws ServiceException
	 */
	public int countPagination(Map<String, Object> valueFilterMap) throws ServiceException;

	/**
	 * Get a select page
	 * 
	 * @param valueFilterMap
	 * @param from
	 *            first element
	 * @param to
	 *            last element
	 * @param orders
	 *            list of orders to apply
	 * @param columns
	 *            columns to fetch. Use null if all columns & children are to fetch
	 * @return
	 * @throws ServiceException
	 */
	public List<E> selectPagination(Map<String, Object> valueFilterMap, int from, int to, List<ISortOrder> orders, Set<String> columns) throws ServiceException;

}
