package com.synaptix.mybatis.helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;

import com.google.inject.Inject;
import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.mybatis.cache.SynaptixCacheManager;
import com.synaptix.mybatis.filter.IFilterContext;
import com.synaptix.mybatis.hack.SynaptixConfiguration;
import com.synaptix.mybatis.helper.ComponentSqlHelper.Join;
import com.synaptix.service.filter.RootNode;
import com.synaptix.service.model.ISortOrder;

public class SelectMappedStatement {

	private static final Log LOG = LogFactory.getLog(SelectMappedStatement.class);

	private static final String VALUE_FILTER_MAP_NAME = "valueFilterMap";

	private static final String FILTER_ROOT_NODE_NAME = "filterRootNode";

	private static final String SORT_ORDERS_NAME = "sortOrders";

	private final ComponentSqlHelper componentSqlHelper;

	private final ComponentColumnsCache componentColumnsCache;

	private final SynaptixCacheManager cacheManager;

	private SynaptixConfiguration synaptixConfiguration;

	private ComponentResultMapHelper componentResultMapHelper;

	@Inject
	public SelectMappedStatement(ComponentSqlHelper componentSqlHelper, ComponentColumnsCache componentColumnsCache, SynaptixCacheManager cacheManager) {
		super();

		this.componentSqlHelper = componentSqlHelper;
		this.componentColumnsCache = componentColumnsCache;
		this.cacheManager = cacheManager;
	}

	@Inject
	public void setConfiguration(SynaptixConfiguration synaptixConfiguration) {
		this.synaptixConfiguration = synaptixConfiguration;
	}

	@Inject
	public void setComponentResultMapHelper(ComponentResultMapHelper componentResultMapHelper) {
		this.componentResultMapHelper = componentResultMapHelper;
	}

	public <E extends IComponent> Map<String, Object> createSelectListParameter(Class<E> componentClass, RootNode filterRootNode, List<ISortOrder> sortOrders) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(FILTER_ROOT_NODE_NAME, filterRootNode);
		map.put(SORT_ORDERS_NAME, sortOrders);
		return map;
	}

	/**
	 * Get a select list mapped statement, if not exists, create a mapped and result
	 * 
	 * @param componentClass
	 * @return
	 */
	public <E extends IComponent> MappedStatement getSelectListMappedStatement(Class<E> componentClass) {
		String key = MappedStatementHelper.buildMappedStatementKey(componentClass, null, "selectList");

		MappedStatement mappedStatement = null;
		if (!synaptixConfiguration.hasComponentStatement(key)) {
			ResultMap inlineResultMap = componentResultMapHelper.getResultMapWithNested(componentClass);

			MappedStatement.Builder msBuilder = new MappedStatement.Builder(synaptixConfiguration, key, new SelectListSqlSource<E>(componentClass), SqlCommandType.SELECT);
			msBuilder.resultMaps(Arrays.asList(inlineResultMap));
			SynaptixCacheManager.CacheResult cacheResult = cacheManager.getCache(componentClass);
			if (cacheResult != null && cacheResult.isEnabled()) {
				msBuilder.flushCacheRequired(false);
				msBuilder.cache(cacheResult.getCache());
				msBuilder.useCache(true);
			}
			mappedStatement = msBuilder.build();
			synaptixConfiguration.addMappedStatement(mappedStatement);
		}
		mappedStatement = synaptixConfiguration.getComponentMappedStatement(key);
		return mappedStatement;
	}

	public <E extends IComponent> Map<String, Object> createSelectOneParameter(Class<E> componentClass, RootNode filterRootNode) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(FILTER_ROOT_NODE_NAME, filterRootNode);
		return map;
	}

	/**
	 * Get a select one mapped statement, if not exists, create a mapped and result
	 * 
	 * @param componentClass
	 * @return
	 */
	public <E extends IComponent> MappedStatement getSelectOneMappedStatement(Class<E> componentClass) {
		String key = MappedStatementHelper.buildMappedStatementKey(componentClass, null, "selectOne");

		MappedStatement mappedStatement = null;
		if (!synaptixConfiguration.hasComponentStatement(key)) {
			ResultMap inlineResultMap = componentResultMapHelper.getResultMapWithNested(componentClass);

			MappedStatement.Builder msBuilder = new MappedStatement.Builder(synaptixConfiguration, key, new SelectOneSqlSource<E>(componentClass), SqlCommandType.SELECT);
			msBuilder.resultMaps(Arrays.asList(inlineResultMap));
			SynaptixCacheManager.CacheResult cacheResult = cacheManager.getCache(componentClass);
			if (cacheResult != null && cacheResult.isEnabled()) {
				msBuilder.flushCacheRequired(false);
				msBuilder.cache(cacheResult.getCache());
				msBuilder.useCache(true);
			}
			mappedStatement = msBuilder.build();
			synaptixConfiguration.addMappedStatement(mappedStatement);
		}
		mappedStatement = synaptixConfiguration.getComponentMappedStatement(key);
		return mappedStatement;
	}

	private <E extends IComponent> BuildSelectOneResult buildSelectOne(Class<E> componentClass, RootNode filterRootNode) {
		if (componentColumnsCache.isValid(componentClass)) {
			return buildNestedSelectOne(componentClass, filterRootNode);
		} else {
			return buildSimpleSelectOne(componentClass, filterRootNode);
		}
	}

	private <E extends IComponent> BuildSelectOneResult buildSimpleSelectOne(Class<E> componentClass, RootNode filterRootNode) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);
		Map<String, Join> joinMap = new HashMap<String, Join>();

		IFilterContext context = componentSqlHelper.createFilterContext(componentClass, joinMap);

		componentSqlHelper.buildFilterJoinMap(componentSqlHelper.buildPropertyNames(context, filterRootNode), joinMap, ed, null, "");

		SQL sqlBuilder = new SQL();
		sqlBuilder.SELECT("t.*");
		String sqlTableName = componentSqlHelper.getSqlTableName(ed);
		sqlBuilder.FROM(new StringBuilder(sqlTableName).append(" t").toString());

		componentSqlHelper.includeOrderedJoinList(sqlBuilder, joinMap);

		String whereFilters = componentSqlHelper.buildWhereSql(context, filterRootNode);
		if (whereFilters != null && !whereFilters.isEmpty()) {
			sqlBuilder.WHERE(whereFilters);
		}

		String sql = sqlBuilder.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}

		return new BuildSelectOneResult(sql, componentSqlHelper.buildValueFilterMap(context, filterRootNode));
	}

	private <E extends IComponent> BuildSelectOneResult buildNestedSelectOne(Class<E> componentClass, RootNode filterRootNode) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);
		Map<String, Join> joinMap = new HashMap<String, Join>();

		IFilterContext context = componentSqlHelper.createFilterContext(componentClass, joinMap);

		Set<String> columns = componentColumnsCache.getPropertyNames(componentClass);

		componentSqlHelper.buildSelectJoinMap(joinMap, ed, null, "", columns, false);
		componentSqlHelper.buildFilterJoinMap(componentSqlHelper.buildPropertyNames(context, filterRootNode), joinMap, ed, null, "");

		SQL sqlBuilder = new SQL();
		componentSqlHelper.selectFields(sqlBuilder, joinMap, ed, null, null, columns, null);
		String sqlTableName = componentSqlHelper.getSqlTableName(ed);
		sqlBuilder.FROM(new StringBuilder(sqlTableName).append(" t").toString());

		componentSqlHelper.includeOrderedJoinList(sqlBuilder, joinMap);

		String whereFilters = componentSqlHelper.buildWhereSql(context, filterRootNode);
		if (whereFilters != null && !whereFilters.isEmpty()) {
			sqlBuilder.WHERE(whereFilters);
		}

		String sql = sqlBuilder.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}

		return new BuildSelectOneResult(sql, componentSqlHelper.buildValueFilterMap(context, filterRootNode));
	}

	private static class BuildSelectOneResult {

		private final String sql;

		private final Map<String, Object> valueFilterMap;

		public BuildSelectOneResult(String sql, Map<String, Object> valueFilterMap) {
			super();
			this.sql = sql;
			this.valueFilterMap = valueFilterMap;
		}

		public String getSql() {
			return sql;
		}

		public Map<String, Object> getValueFilterMap() {
			return valueFilterMap;
		}
	}

	private <E extends IComponent> BuildSelectListResult buildSelectList(Class<E> componentClass, RootNode filterRootNode, List<ISortOrder> sortOrders) {
		if (componentColumnsCache.isValid(componentClass)) {
			return buildNestedSelectList(componentClass, filterRootNode, sortOrders);
		} else {
			return buildSimpleSelectList(componentClass, filterRootNode, sortOrders);
		}
	}

	private <E extends IComponent> BuildSelectListResult buildSimpleSelectList(Class<E> componentClass, RootNode filterRootNode, List<ISortOrder> sortOrders) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);
		Map<String, Join> joinMap = new HashMap<String, Join>();

		IFilterContext context = componentSqlHelper.createFilterContext(componentClass, joinMap);

		componentSqlHelper.buildFilterJoinMap(componentSqlHelper.buildPropertyNames(context, filterRootNode), joinMap, ed, null, "");
		componentSqlHelper.buildOrderJoinMap(sortOrders, joinMap, ed, null, "");

		SQL sqlBuilder = new SQL();
		sqlBuilder.SELECT("t.*");
		String sqlTableName = componentSqlHelper.getSqlTableName(ed);
		sqlBuilder.FROM(new StringBuilder(sqlTableName).append(" t").toString());

		componentSqlHelper.includeOrderedJoinList(sqlBuilder, joinMap);

		String whereFilters = componentSqlHelper.buildWhereSql(context, filterRootNode);
		if (whereFilters != null && !whereFilters.isEmpty()) {
			sqlBuilder.WHERE(whereFilters);
		}

		componentSqlHelper.orderBy(sqlBuilder, sortOrders, joinMap, ed);

		String sql = sqlBuilder.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}

		return new BuildSelectListResult(sql, componentSqlHelper.buildValueFilterMap(context, filterRootNode));
	}

	private <E extends IComponent> BuildSelectListResult buildNestedSelectList(Class<E> componentClass, RootNode filterRootNode, List<ISortOrder> sortOrders) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);
		Map<String, Join> joinMap = new HashMap<String, Join>();

		IFilterContext context = componentSqlHelper.createFilterContext(componentClass, joinMap);

		Set<String> columns = componentColumnsCache.getPropertyNames(componentClass);

		componentSqlHelper.buildSelectJoinMap(joinMap, ed, null, "", columns, false);
		componentSqlHelper.buildFilterJoinMap(componentSqlHelper.buildPropertyNames(context, filterRootNode), joinMap, ed, null, "");
		componentSqlHelper.buildOrderJoinMap(sortOrders, joinMap, ed, null, "");

		SQL sqlBuilder = new SQL();
		componentSqlHelper.selectFields(sqlBuilder, joinMap, ed, null, null, columns, null);
		String sqlTableName = componentSqlHelper.getSqlTableName(ed);
		sqlBuilder.FROM(new StringBuilder(sqlTableName).append(" t").toString());

		componentSqlHelper.includeOrderedJoinList(sqlBuilder, joinMap);

		String whereFilters = componentSqlHelper.buildWhereSql(context, filterRootNode);
		if (whereFilters != null && !whereFilters.isEmpty()) {
			sqlBuilder.WHERE(whereFilters);
		}

		componentSqlHelper.orderBy(sqlBuilder, sortOrders, joinMap, ed);

		String sql = sqlBuilder.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}

		return new BuildSelectListResult(sql, componentSqlHelper.buildValueFilterMap(context, filterRootNode));
	}

	private static class BuildSelectListResult {

		private final String sql;

		private final Map<String, Object> valueFilterMap;

		public BuildSelectListResult(String sql, Map<String, Object> valueFilterMap) {
			super();
			this.sql = sql;
			this.valueFilterMap = valueFilterMap;
		}

		public String getSql() {
			return sql;
		}

		public Map<String, Object> getValueFilterMap() {
			return valueFilterMap;
		}
	}

	private class SelectListSqlSource<E extends IComponent> implements SqlSource {

		private final Class<E> componentClass;

		private final SqlSourceBuilder sqlSourceParser;

		public SelectListSqlSource(Class<E> componentClass) {
			super();

			this.componentClass = componentClass;
			this.sqlSourceParser = new SqlSourceBuilder(synaptixConfiguration);
		}

		@Override
		public BoundSql getBoundSql(Object parameterObject) {
			SqlSource sqlSource = createSqlSource(parameterObject);
			return sqlSource.getBoundSql(parameterObject);
		}

		@SuppressWarnings("unchecked")
		private SqlSource createSqlSource(Object parameterObject) {
			try {
				Map<String, Object> map = (Map<String, Object>) parameterObject;
				BuildSelectListResult buildSelectListResult = buildSelectList(componentClass, (RootNode) map.get(FILTER_ROOT_NODE_NAME), (List<ISortOrder>) map.get(SORT_ORDERS_NAME));
				map.put(VALUE_FILTER_MAP_NAME, buildSelectListResult.getValueFilterMap());
				String sql = buildSelectListResult.getSql();
				Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
				return sqlSourceParser.parse(sql, parameterType, null);
			} catch (Exception e) {
				throw new BuilderException("Error invoking Select method for SelectList Cause: " + e, e);
			}
		}
	}

	private class SelectOneSqlSource<E extends IComponent> implements SqlSource {

		private final Class<E> componentClass;

		private final SqlSourceBuilder sqlSourceParser;

		public SelectOneSqlSource(Class<E> componentClass) {
			super();

			this.componentClass = componentClass;
			this.sqlSourceParser = new SqlSourceBuilder(synaptixConfiguration);
		}

		@Override
		public BoundSql getBoundSql(Object parameterObject) {
			SqlSource sqlSource = createSqlSource(parameterObject);
			return sqlSource.getBoundSql(parameterObject);
		}

		@SuppressWarnings("unchecked")
		private SqlSource createSqlSource(Object parameterObject) {
			try {
				Map<String, Object> map = (Map<String, Object>) parameterObject;
				BuildSelectOneResult buildSelectOneResult = buildSelectOne(componentClass, (RootNode) map.get(FILTER_ROOT_NODE_NAME));
				map.put(VALUE_FILTER_MAP_NAME, buildSelectOneResult.getValueFilterMap());
				String sql = buildSelectOneResult.getSql();
				Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
				return sqlSourceParser.parse(sql, parameterType, null);
			} catch (Exception e) {
				throw new BuilderException("Error invoking Select method for SelectOne Cause: " + e, e);
			}
		}
	}
}
