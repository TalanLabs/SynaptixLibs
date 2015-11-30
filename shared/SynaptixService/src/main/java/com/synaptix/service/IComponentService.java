package com.synaptix.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.synaptix.component.IComponent;
import com.synaptix.entity.IId;
import com.synaptix.service.filter.RootNode;
import com.synaptix.service.model.ISortOrder;

public interface IComponentService {

	/**
	 * Count for componentClass, Old version
	 * 
	 * @param componentClass
	 * @param valueFilterMap
	 * @return
	 * @throws ServiceException
	 */
	public <E extends IComponent> int countPaginationOld(Class<E> componentClass, Map<String, Object> valueFilterMap) throws ServiceException;

	/**
	 * Select a page for componentClass, Old version
	 * 
	 * @param componentClass
	 * @param valueFilterMap
	 * @param from
	 * @param to
	 * @param sortOrders
	 * @param columns
	 * @return
	 * @throws ServiceException
	 */
	public <E extends IComponent> List<E> selectPaginationOld(Class<E> componentClass, Map<String, Object> valueFilterMap, int from, int to, List<ISortOrder> sortOrders, Set<String> columns)
			throws ServiceException;

	/**
	 * Find list of component by id parent
	 * 
	 * @param componentClass
	 * @param idParentPropertyName
	 * @param idParent
	 * @return
	 * @throws ServiceException
	 */
	public <E extends IComponent> List<E> findComponentsByIdParent(Class<E> componentClass, String idParentPropertyName, IId idParent) throws ServiceException;

	/**
	 * Get a select suggest, 5 elements
	 * 
	 * @param componentClass
	 * @param filterRootNode
	 * @param sortOrders
	 *            list of orders to apply
	 * @param columns
	 * @return
	 * @throws ServiceException
	 */
	public <E extends IComponent> List<E> selectSuggest(Class<E> componentClass, RootNode filterRootNode, List<ISortOrder> sortOrders, Set<String> columns) throws ServiceException;

	/**
	 * Select a list of component with filter and sort
	 * 
	 * @param componentClass
	 * @param filterRootNode
	 * @param sortOrders
	 * @return
	 * @throws ServiceException
	 */
	public <E extends IComponent> List<E> selectList(Class<E> componentClass, RootNode filterRootNode, List<ISortOrder> sortOrders) throws ServiceException;

	/**
	 * Select a one component with filter
	 * 
	 * @param componentClass
	 * @param filterRootNode
	 * @return
	 * @throws ServiceException
	 */
	public <E extends IComponent> E selectOne(Class<E> componentClass, RootNode filterRootNode) throws ServiceException;

	/**
	 * Count for componentClass
	 * 
	 * @param componentClass
	 * @param filterRootNode
	 * @return
	 * @throws ServiceException
	 */
	public <E extends IComponent> int countPagination(Class<E> componentClass, RootNode filterRootNode) throws ServiceException;

	/**
	 * Select a page for componentClass
	 * 
	 * @param componentClass
	 * @param filterRootNode
	 * @param from
	 * @param to
	 * @param sortOrders
	 * @param columns
	 * @return
	 * @throws ServiceException
	 */
	public <E extends IComponent> List<E> selectPagination(Class<E> componentClass, RootNode filterRootNode, int from, int to, List<ISortOrder> sortOrders, Set<String> columns)
			throws ServiceException;
}
