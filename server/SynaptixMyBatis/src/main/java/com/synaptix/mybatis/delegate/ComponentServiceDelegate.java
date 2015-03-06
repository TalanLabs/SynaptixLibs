package com.synaptix.mybatis.delegate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.mapping.MappedStatement;

import com.google.inject.Inject;
import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.mybatis.dao.IDaoSession;
import com.synaptix.mybatis.helper.FindMappedStatement;
import com.synaptix.mybatis.helper.PaginationMappedStatement;
import com.synaptix.mybatis.helper.PaginationOldMappedStatement;
import com.synaptix.mybatis.helper.SelectMappedStatement;
import com.synaptix.mybatis.helper.SelectNestedMappedStatement;
import com.synaptix.mybatis.helper.SelectSqlMappedStatement;
import com.synaptix.mybatis.helper.SimplePaginationMappedStatement;
import com.synaptix.service.ServiceException;
import com.synaptix.service.filter.RootNode;
import com.synaptix.service.model.ISortOrder;

public class ComponentServiceDelegate {

	@Inject
	private IDaoSession daoSession;

	@Inject
	private SelectMappedStatement selectMappedStatement;

	@Inject
	private PaginationOldMappedStatement paginationOldMappedStatement;

	@Inject
	private SimplePaginationMappedStatement simplePaginationMappedStatement;

	@Inject
	private SelectNestedMappedStatement selectNestedMappedStatement;

	@Inject
	private FindMappedStatement findMappedStatement;

	@Inject
	private PaginationMappedStatement paginationMappedStatement;

	@Inject
	private SelectSqlMappedStatement selectSqlMappedStatement;

	@Inject
	public ComponentServiceDelegate() {
		super();
	}

	public <E extends IComponent> int countPaginationOld(Class<E> componentClass, Map<String, Object> valueFilterMap) throws ServiceException {
		MappedStatement mappedStatement = paginationOldMappedStatement.getCountPaginationMappedStatementOld(componentClass);
		return daoSession.getSqlSession().<Integer> selectOne(mappedStatement.getId(), paginationOldMappedStatement.createCountPaginationParameterOld(componentClass, valueFilterMap));
	}

	/**
	 * Select list of component pagination, use nested result
	 * 
	 * @param componentClass
	 * @param valueFilterMap
	 * @param from
	 * @param to
	 * @param sortOrders
	 * @param columns
	 * @return
	 */
	public <E extends IComponent> List<E> selectPaginationOld(Class<E> componentClass, Map<String, Object> valueFilterMap, int from, int to, List<ISortOrder> sortOrders, Set<String> columns)
			throws ServiceException {
		return selectPaginationOld(componentClass, valueFilterMap, from, to, sortOrders, columns, true);
	}

	/**
	 * Select list of component pagination, use nested result
	 * 
	 * @param componentClass
	 * @param valueFilterMap
	 * @param from
	 * @param to
	 * @param sortOrders
	 * @param columns
	 * @param nested
	 *            nested sql or lazy loading
	 * @return
	 */
	public <E extends IComponent> List<E> selectPaginationOld(Class<E> componentClass, Map<String, Object> valueFilterMap, int from, int to, List<ISortOrder> sortOrders, Set<String> columns,
			boolean nested) throws ServiceException {
		int maxRow = to - from + 1;
		if (maxRow > 100) {
			maxRow = 100;
		}
		if (nested) {
			MappedStatement mappedStatement = paginationOldMappedStatement.getSelectPaginationMappedStatementOld(componentClass, columns, maxRow);
			return daoSession.getSqlSession().<E> selectList(mappedStatement.getId(),
					paginationOldMappedStatement.createSelectPaginationParameterOld(componentClass, valueFilterMap, from, to, sortOrders));
		} else {
			MappedStatement mappedStatement = simplePaginationMappedStatement.getSelectSimplePaginationMappedStatement(componentClass, maxRow);
			List<E> res = daoSession.getSqlSession().<E> selectList(mappedStatement.getId(),
					simplePaginationMappedStatement.createSelectSimplePaginationParameter(componentClass, valueFilterMap, from, to, sortOrders));
			if (res != null && !res.isEmpty()) {
				List<E> copies = new ArrayList<E>();
				for (E e : res) {
					E copy = null;
					if (e != null) {
						copy = ComponentFactory.getInstance().createInstance(componentClass);
						ComponentHelper.copy(e, copy, columns);
					}
					copies.add(copy);
				}
				res = copies;
			}
			return res;
		}
	}

	/**
	 * Find a component by propertyName,
	 * 
	 * @param componentClass
	 * @param idPropertyName
	 * @param id
	 * @return
	 */
	public <E extends IComponent> E findComponentByPropertyName(Class<E> componentClass, String idPropertyName, Serializable id) throws ServiceException {
		MappedStatement mappedStatement = findMappedStatement.getFindComponentsByPropertyNameMappedStatement(componentClass, idPropertyName, false);
		return daoSession.getSqlSession().<E> selectOne(mappedStatement.getId(), id);
	}

	/**
	 * Find list of component by propertyName
	 * 
	 * @param componentClass
	 * @param idParentPropertyName
	 * @param idParent
	 * @return
	 */
	public <E extends IComponent> List<E> findComponentsByIdParent(Class<E> componentClass, String idParentPropertyName, Serializable idParent) throws ServiceException {
		MappedStatement mappedStatement = findMappedStatement.getFindComponentsByPropertyNameMappedStatement(componentClass, idParentPropertyName, true);
		return daoSession.getSqlSession().<E> selectList(mappedStatement.getId(), idParent);
	}

	/**
	 * Select five component, use nested result
	 * 
	 * @param componentClass
	 * @param filterRootNode
	 * @param sortOrders
	 * @param columns
	 * @return
	 * @throws ServiceException
	 */
	public <E extends IComponent> List<E> selectSuggest(Class<E> componentClass, RootNode filterRootNode, List<ISortOrder> sortOrders, Set<String> columns) throws ServiceException {
		MappedStatement mappedStatement = selectNestedMappedStatement.getSelectSuggestMappedStatement(componentClass, columns, 5 - 1 + 1);
		return daoSession.getSqlSession().<E> selectList(mappedStatement.getId(), selectNestedMappedStatement.createSelectSuggestParameter(componentClass, filterRootNode, 1, 5, sortOrders));
	}

	/**
	 * Select list of components
	 * 
	 * @param componentClass
	 * @param filterRootNode
	 * @param sortOrders
	 * @return
	 */
	public <E extends IComponent> List<E> selectList(Class<E> componentClass, RootNode filterRootNode, List<ISortOrder> sortOrders) throws ServiceException {
		MappedStatement mappedStatement = selectMappedStatement.getSelectListMappedStatement(componentClass);
		return daoSession.getSqlSession().<E> selectList(mappedStatement.getId(), selectMappedStatement.createSelectListParameter(componentClass, filterRootNode, sortOrders));
	}

	/**
	 * Select one component
	 * 
	 * @param componentClass
	 * @param filterRootNode
	 * @return
	 */
	public <E extends IComponent> E selectOne(Class<E> componentClass, RootNode filterRootNode) throws ServiceException {
		MappedStatement mappedStatement = selectMappedStatement.getSelectOneMappedStatement(componentClass);
		return daoSession.getSqlSession().<E> selectOne(mappedStatement.getId(), selectMappedStatement.createSelectOneParameter(componentClass, filterRootNode));
	}

	/**
	 * Count for componentClass
	 * 
	 * @param componentClass
	 * @param filterRootNode
	 * @return
	 * @throws ServiceException
	 */
	public <E extends IComponent> int countPagination(Class<E> componentClass, RootNode filterRootNode) throws ServiceException {
		MappedStatement mappedStatement = paginationMappedStatement.getCountPaginationMappedStatement(componentClass);
		return daoSession.getSqlSession().<Integer> selectOne(mappedStatement.getId(), paginationMappedStatement.createCountPaginationParameter(componentClass, filterRootNode));
	}

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
			throws ServiceException {
		MappedStatement mappedStatement = paginationMappedStatement.getSelectPaginationMappedStatement(componentClass, columns, to - from + 1);
		return daoSession.getSqlSession().<E> selectList(mappedStatement.getId(),
				paginationMappedStatement.createSelectPaginationParameter(componentClass, filterRootNode, from, to, sortOrders, columns));
	}

	/**
	 * Select list of components with sql
	 * 
	 * @param componentClass
	 * @param sql
	 * @param value
	 * @return
	 */
	public <E extends IComponent> List<E> selectListSql(Class<E> componentClass, String sql, Object value) throws ServiceException {
		MappedStatement mappedStatement = selectSqlMappedStatement.getSelectSqlMappedStatement(componentClass, sql);
		return daoSession.getSqlSession().<E> selectList(mappedStatement.getId(), value);
	}

	/**
	 * Select one component with sql
	 * 
	 * @param componentClass
	 * @param sql
	 * @param value
	 * @return
	 */
	public <E extends IComponent> E selectOneSql(Class<E> componentClass, String sql, Object value) throws ServiceException {
		MappedStatement mappedStatement = selectSqlMappedStatement.getSelectSqlMappedStatement(componentClass, sql);
		return daoSession.getSqlSession().<E> selectOne(mappedStatement.getId(), value);
	}

	/**
	 * Select list of type with sql
	 * 
	 * @param componentClass
	 * @param typeClass
	 * @param sql
	 * @param value
	 * @return
	 */
	public <E extends IComponent, F> List<F> selectListSql(Class<E> componentClass, Class<F> typeClass, String sql, Object value) throws ServiceException {
		MappedStatement mappedStatement = selectSqlMappedStatement.getSelectSqlMappedStatement(componentClass, typeClass, sql);
		return daoSession.getSqlSession().<F> selectList(mappedStatement.getId(), value);
	}

	/**
	 * Select one type with sql
	 * 
	 * @param componentClass
	 * @param typeClass
	 * @param sql
	 * @param value
	 * @return
	 */
	public <E extends IComponent, F> F selectOneSql(Class<E> componentClass, Class<F> typeClass, String sql, Object value) throws ServiceException {
		MappedStatement mappedStatement = selectSqlMappedStatement.getSelectSqlMappedStatement(componentClass, typeClass, sql);
		return daoSession.getSqlSession().<F> selectOne(mappedStatement.getId(), value);
	}
}
