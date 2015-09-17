package com.synaptix.mybatis.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
import org.apache.ibatis.mapping.ResultMapping;
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
import com.synaptix.service.helper.SortOrderHelper;
import com.synaptix.service.model.ISortOrder;

public class PaginationMappedStatement {

	private static final Log LOG = LogFactory.getLog(PaginationOldMappedStatement.class);

	private static final String VALUE_FILTER_MAP_NAME = "valueFilterMap";

	private static final String FILTER_ROOT_NODE_NAME = "filterRootNode";

	private static final String TO_NAME = "to";

	private static final String FROM_NAME = "from";

	private static final String SORT_ORDERS_NAME = "sortOrders";

	private static final String COLUMNS_NAME = "columns";

	private final ComponentSqlHelper componentSqlHelper;

	private final SynaptixCacheManager cacheManager;

	private ComponentResultMapHelper componentResultMapHelper;

	private SynaptixConfiguration synaptixConfiguration;

	@Inject
	public PaginationMappedStatement(ComponentSqlHelper componentSqlHelper, SynaptixCacheManager cacheManager) {
		super();

		this.componentSqlHelper = componentSqlHelper;
		this.cacheManager = cacheManager;
	}

	@Inject
	public void setConfiguration(SynaptixConfiguration configuration) {
		this.synaptixConfiguration = configuration;
	}

	@Inject
	public void setComponentResultMapHelper(ComponentResultMapHelper componentResultMapHelper) {
		this.componentResultMapHelper = componentResultMapHelper;
	}

	public <E extends IComponent> Map<String, Object> createCountPaginationParameter(Class<E> componentClass, RootNode filterRootNode) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(FILTER_ROOT_NODE_NAME, filterRootNode);
		return map;
	}

	public <E extends IComponent> MappedStatement getCountPaginationMappedStatement(Class<E> componentClass) {
		String key = MappedStatementHelper.buildMappedStatementKey(componentClass, null, "countPagination");

		MappedStatement mappedStatement = null;
		if (!synaptixConfiguration.hasComponentStatement(key)) {
			MappedStatement.Builder msBuilder = new MappedStatement.Builder(synaptixConfiguration, key, new CountPaginationSqlSource<E>(componentClass), SqlCommandType.SELECT);
			ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(synaptixConfiguration, key + "-Inline", Integer.class, new ArrayList<ResultMapping>(), null);
			msBuilder.resultMaps(Arrays.asList(inlineResultMapBuilder.build()));
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

	public <E extends IComponent> Map<String, Object> createSelectPaginationParameter(Class<E> componentClass, RootNode filterRootNode, int from, int to, List<ISortOrder> sortOrders,
			Set<String> columns) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(FILTER_ROOT_NODE_NAME, filterRootNode);
		map.put(FROM_NAME, from);
		map.put(TO_NAME, to);
		map.put(SORT_ORDERS_NAME, sortOrders);
		map.put(COLUMNS_NAME, columns);
		return map;
	}

	public <E extends IComponent> MappedStatement getSelectPaginationMappedStatement(Class<E> componentClass, Set<String> columns, int maxRow) {
		Set<String> cs = new HashSet<String>(columns);
		cs.add("maxRow-" + maxRow);
		String key = MappedStatementHelper.buildMappedStatementKey(componentClass, cs, "selectPagination");

		MappedStatement mappedStatement = null;
		if (!synaptixConfiguration.hasComponentStatement(key)) {
			ResultMap inlineResultMap = componentResultMapHelper.getNestedResultMap(componentClass, columns);

			MappedStatement.Builder msBuilder = new MappedStatement.Builder(synaptixConfiguration, key, new SelectPaginationNestedSqlSource<E>(componentClass), SqlCommandType.SELECT);
			msBuilder.resultMaps(Arrays.asList(inlineResultMap));
			SynaptixCacheManager.CacheResult cacheResult = cacheManager.getCache(componentClass);
			if (cacheResult != null && cacheResult.isEnabled()) {
				msBuilder.flushCacheRequired(false);
				msBuilder.cache(cacheResult.getCache());
				msBuilder.fetchSize(maxRow);
				msBuilder.useCache(true);
			}
			mappedStatement = msBuilder.build();
			synaptixConfiguration.addMappedStatement(mappedStatement);
		}
		mappedStatement = synaptixConfiguration.getComponentMappedStatement(key);
		return mappedStatement;
	}

	/**
	 * Build a count sql
	 *
	 * @param componentClass
	 * @param valueFilterMap
	 * @return
	 */
	private <E extends IComponent> BuildCountPaginationResult buildCountPagination(Class<E> componentClass, RootNode filterRootNode) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);
		Map<String, Join> joinMap = new HashMap<String, Join>();

		IFilterContext context = componentSqlHelper.createFilterContext(componentClass, joinMap);

		componentSqlHelper.buildFilterJoinMap(componentSqlHelper.buildPropertyNames(context, filterRootNode), joinMap, ed, null, "");

		SQL sqlBuilder = new SQL();
		sqlBuilder.SELECT("count(0)");
		sqlBuilder.FROM(new StringBuilder(componentSqlHelper.getSqlTableName(ed)).append(" t").toString());

		componentSqlHelper.includeOrderedJoinList(sqlBuilder, joinMap);

		String whereFilters = componentSqlHelper.buildWhereSql(context, filterRootNode);
		if (whereFilters != null && !whereFilters.isEmpty()) {
			sqlBuilder.WHERE(whereFilters);
		}

		String sql = sqlBuilder.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}

		return new BuildCountPaginationResult(sql, componentSqlHelper.buildValueFilterMap(context, filterRootNode));
	}

	private static class BuildCountPaginationResult {

		private final String sql;

		private final Map<String, Object> valueFilterMap;

		public BuildCountPaginationResult(String sql, Map<String, Object> valueFilterMap) {
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

	/**
	 * Build a select sql
	 *
	 * @param entityClass
	 * @param valueFilterMap
	 * @param sortOrders
	 * @param columns
	 * @return
	 */
	private <E extends IComponent> BuildSelectPaginationNestedResult buildSelectPagination(Class<E> componentClass, RootNode filterRootNode, List<ISortOrder> sortOrders, Set<String> columns) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);

		Map<String, Join> joinMap = new HashMap<String, Join>();
		IFilterContext context = componentSqlHelper.createFilterContext(componentClass, joinMap);

		componentSqlHelper.buildSelectJoinMap(joinMap, ed, null, "", columns, true);

		SQL sqlBuilder = new SQL();
		componentSqlHelper.selectFields(sqlBuilder, joinMap, ed, null, null, columns, null);
		if (!joinMap.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			componentSqlHelper.selectFields(sqlBuilder, joinMap, ed, null, null, columns, sb);
			if (sb.length() > 0) {
				sqlBuilder.SELECT(sb.toString());
			}
		}
		String sqlTableName = componentSqlHelper.getSqlTableName(ed);
		sqlBuilder.FROM(new StringBuilder("(SELECT t.* FROM (SELECT t.*, ROWNUM AS rn FROM (").append(buildFirstSelect(componentClass, filterRootNode, sortOrders))
				.append(") t WHERE #{to,javaType=int} >= ROWNUM) t WHERE rn >= #{from,javaType=int}) i, ").append(sqlTableName).append(" t").toString());
		componentSqlHelper.includeOrderedJoinList(sqlBuilder, joinMap);
		sqlBuilder.WHERE("(i.a_rowid = t.ROWID)");
		sqlBuilder.ORDER_BY("i.rn");

		String sql = sqlBuilder.toString();

		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}

		return new BuildSelectPaginationNestedResult(sql, componentSqlHelper.buildValueFilterMap(context, filterRootNode));
	}

	private <E extends IComponent> String buildFirstSelect(Class<E> componentClass, RootNode filterRootNode, List<ISortOrder> sortOrders) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);

		Map<String, Join> joinMap = new HashMap<String, Join>();
		IFilterContext context = componentSqlHelper.createFilterContext(componentClass, joinMap);
		if (sortOrders != null && !sortOrders.isEmpty()) {
			componentSqlHelper.buildSelectJoinMap(joinMap, ed, null, "", SortOrderHelper.getInstance().extractColumns(sortOrders), false);
		}
		componentSqlHelper.buildFilterJoinMap(componentSqlHelper.buildPropertyNames(context, filterRootNode), joinMap, ed, null, "");
		componentSqlHelper.buildOrderJoinMap(sortOrders, joinMap, ed, null, "");

		SQL sqlBuilder = new SQL();
		String hint = componentSqlHelper.buildHint(ed, filterRootNode);
		sqlBuilder.SELECT(hint + " t.ROWID AS a_rowid");
		String sqlTableName = componentSqlHelper.getSqlTableName(ed);
		sqlBuilder.FROM(new StringBuilder(sqlTableName).append(" t").toString());

		componentSqlHelper.includeOrderedJoinList(sqlBuilder, joinMap);

		String whereFilters = componentSqlHelper.buildWhereSql(context, filterRootNode);
		if (whereFilters != null && !whereFilters.isEmpty()) {
			sqlBuilder.WHERE(whereFilters);
		}

		// TODO

		componentSqlHelper.orderBy(sqlBuilder, sortOrders, joinMap, ed);

		return sqlBuilder.toString();
	}

	private static class BuildSelectPaginationNestedResult {

		private final String sql;

		private final Map<String, Object> valueFilterMap;

		public BuildSelectPaginationNestedResult(String sql, Map<String, Object> valueFilterMap) {
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

	private class CountPaginationSqlSource<E extends IComponent> implements SqlSource {

		private final Class<E> componentClass;

		private final SqlSourceBuilder sqlSourceParser;

		public CountPaginationSqlSource(Class<E> componentClass) {
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
				BuildCountPaginationResult buildCountPaginationResult = buildCountPagination(componentClass, (RootNode) map.get(FILTER_ROOT_NODE_NAME));
				map.put(VALUE_FILTER_MAP_NAME, buildCountPaginationResult.getValueFilterMap());
				String sql = buildCountPaginationResult.getSql();
				Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
				return sqlSourceParser.parse(sql, parameterType, null);
			} catch (Exception e) {
				throw new BuilderException("Error invoking Count method for CountPagination Cause: " + e, e);
			}
		}
	}

	private class SelectPaginationNestedSqlSource<E extends IComponent> implements SqlSource {

		private final Class<E> componentClass;

		private final SqlSourceBuilder sqlSourceParser;

		public SelectPaginationNestedSqlSource(Class<E> componentClass) {
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
				BuildSelectPaginationNestedResult buildSelectPaginationNestedResult = buildSelectPagination(componentClass, (RootNode) map.get(FILTER_ROOT_NODE_NAME),
						(List<ISortOrder>) map.get(SORT_ORDERS_NAME), (Set<String>) map.get(COLUMNS_NAME));
				map.put(VALUE_FILTER_MAP_NAME, buildSelectPaginationNestedResult.getValueFilterMap());
				String sql = buildSelectPaginationNestedResult.getSql();
				Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
				return sqlSourceParser.parse(sql, parameterType, null);
			} catch (Exception e) {
				throw new BuilderException("Error invoking Select method for SelectPaginationNested Cause: " + e, e);
			}
		}
	}
}
