package com.synaptix.mybatis.helper;

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

public class SelectNestedMappedStatement {

	private static final Log LOG = LogFactory.getLog(SelectNestedMappedStatement.class);

	private static final String VALUE_FILTER_MAP_NAME = "valueFilterMap";

	private static final String FILTER_ROOT_NODE_NAME = "filterRootNode";

	private static final String TO_NAME = "to";

	private static final String FROM_NAME = "from";

	private static final String SORT_ORDERS_NAME = "sortOrders";

	private final ComponentSqlHelper componentSqlHelper;

	private final SynaptixCacheManager cacheManager;

	private final ComponentColumnsCache componentColumnsCache;

	private SynaptixConfiguration synaptixConfiguration;

	private ComponentResultMapHelper componentResultMapHelper;

	@Inject
	public SelectNestedMappedStatement(ComponentSqlHelper componentSqlHelper, ComponentColumnsCache componentColumnsCache, SynaptixCacheManager cacheManager) {
		super();

		this.componentSqlHelper = componentSqlHelper;
		this.componentColumnsCache = componentColumnsCache;
		this.cacheManager = cacheManager;
	}

	@Inject
	public void setSynaptixConfiguration(SynaptixConfiguration synaptixConfiguration) {
		this.synaptixConfiguration = synaptixConfiguration;
	}

	@Inject
	public void setComponentResultMapHelper(ComponentResultMapHelper componentResultMapHelper) {
		this.componentResultMapHelper = componentResultMapHelper;
	}

	public <E extends IComponent> Map<String, Object> createSelectSuggestParameter(Class<E> componentClass, RootNode filterRootNode, int from, int to, List<ISortOrder> sortOrders) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(FILTER_ROOT_NODE_NAME, filterRootNode);
		map.put(FROM_NAME, from);
		map.put(TO_NAME, to);
		map.put(SORT_ORDERS_NAME, sortOrders);
		return map;
	}

	/**
	 * Get a suggest mapped statement, if not exists, create a nested mapped and nested result
	 * 
	 * @param componentClass
	 * @param columns
	 * @param maxRow
	 * @return
	 */
	public <E extends IComponent> MappedStatement getSelectSuggestMappedStatement(Class<E> componentClass, Set<String> columns, int maxRow) {
		Set<String> cs = new HashSet<String>();
		if (columns != null) {
			cs.addAll(columns);
		}
		cs.add("maxRow-" + maxRow);

		String key = MappedStatementHelper.buildMappedStatementKey(componentClass, cs, "selectSuggest");

		MappedStatement mappedStatement = null;
		if (!synaptixConfiguration.hasComponentStatement(key)) {
			ResultMap inlineResultMap;
			if (columns == null) {
				inlineResultMap = componentResultMapHelper.getResultMapWithNested(componentClass);
			} else {
				inlineResultMap = componentResultMapHelper.getNestedResultMap(componentClass, columns);
			}

			MappedStatement.Builder msBuilder = new MappedStatement.Builder(synaptixConfiguration, key, new SelectSuggestNestedSqlSource<E>(componentClass, columns), SqlCommandType.SELECT);
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

	private <E extends IComponent> BuildSelectSuggestNestedResult buildSelectSuggest(Class<E> componentClass, RootNode filterRootNode, List<ISortOrder> sortOrders, Set<String> columns) {
		if (columns == null) {
			if (componentColumnsCache.isValid(componentClass)) {
				return buildNestedSelectSuggest(componentClass, filterRootNode, sortOrders);
			} else {
				return buildSimpleSelectSuggest(componentClass, filterRootNode, sortOrders);
			}
		} else {
			return buildNestedSelectSuggest(componentClass, filterRootNode, sortOrders, columns);
		}
	}

	private <E extends IComponent> BuildSelectSuggestNestedResult buildSimpleSelectSuggest(Class<E> componentClass, RootNode filterRootNode, List<ISortOrder> sortOrders) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);

		Map<String, Join> joinMap = new HashMap<String, Join>();
		IFilterContext context = componentSqlHelper.createFilterContext(componentClass, joinMap);

		String sqlTableName = componentSqlHelper.getSqlTableName(ed);

		SQL sqlBuilder = new SQL();
		sqlBuilder.SELECT("t.*");
		String firstSelect = buildFirstSelect(componentClass, filterRootNode, sortOrders);

		buildSelect(sqlTableName, firstSelect, joinMap, sqlBuilder);

		String sql = sqlBuilder.toString();

		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}

		return new BuildSelectSuggestNestedResult(sql, componentSqlHelper.buildValueFilterMap(context, filterRootNode));
	}

	private <E extends IComponent> BuildSelectSuggestNestedResult buildNestedSelectSuggest(Class<E> componentClass, RootNode filterRootNode, List<ISortOrder> sortOrders) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);

		Map<String, Join> joinMap = new HashMap<String, Join>();
		IFilterContext context = componentSqlHelper.createFilterContext(componentClass, joinMap);
		Set<String> columns = componentColumnsCache.getPropertyNames(componentClass);
		componentSqlHelper.buildSelectJoinMap(joinMap, ed, null, "", columns, false);

		String sqlTableName = componentSqlHelper.getSqlTableName(ed);

		SQL sqlBuilder = new SQL();
		componentSqlHelper.selectFields(sqlBuilder, joinMap, ed, null, null, columns, null);

		String firstSelect = buildFirstSelect(componentClass, filterRootNode, sortOrders);

		buildSelect(sqlTableName, firstSelect, joinMap, sqlBuilder);

		String sql = sqlBuilder.toString();

		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}

		return new BuildSelectSuggestNestedResult(sql, componentSqlHelper.buildValueFilterMap(context, filterRootNode));
	}

	protected void buildSelect(String sqlTableName, String firstSelect, Map<String, Join> joinMap, SQL sqlBuilder) {
		sqlBuilder.FROM(new StringBuilder("(SELECT t.* FROM (SELECT t.*, ROWNUM AS rn FROM (").append(firstSelect)
				.append(") t WHERE #{to,javaType=int} >= ROWNUM) t WHERE rn >= #{from,javaType=int}) i, ").append(sqlTableName).append(" t").toString());
		componentSqlHelper.includeOrderedJoinList(sqlBuilder, joinMap);
		sqlBuilder.WHERE("(i.a_rowid = t." + synaptixConfiguration.getRowidName() + ")");
		sqlBuilder.ORDER_BY("i.rn");
	}

	private <E extends IComponent> BuildSelectSuggestNestedResult buildNestedSelectSuggest(Class<E> componentClass, RootNode filterRootNode, List<ISortOrder> sortOrders, Set<String> columns) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);

		Map<String, Join> joinMap = new HashMap<String, Join>();
		IFilterContext context = componentSqlHelper.createFilterContext(componentClass, joinMap);

		componentSqlHelper.buildSelectJoinMap(joinMap, ed, null, "", columns, true);

		String sqlTableName = componentSqlHelper.getSqlTableName(ed);

		SQL sqlBuilder = new SQL();
		componentSqlHelper.selectFields(sqlBuilder, joinMap, ed, null, null, columns, null);
		if (!joinMap.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			componentSqlHelper.selectFields(sqlBuilder, joinMap, ed, null, null, columns, sb);
			if (sb.length() > 0) {
				sqlBuilder.SELECT(sb.toString());
			}
		}

		String firstSelect = buildFirstSelect(componentClass, filterRootNode, sortOrders);

		buildSelect(sqlTableName, firstSelect, joinMap, sqlBuilder);

		String sql = sqlBuilder.toString();

		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}

		return new BuildSelectSuggestNestedResult(sql, componentSqlHelper.buildValueFilterMap(context, filterRootNode));
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
		sqlBuilder.SELECT("t." + synaptixConfiguration.getRowidName() + " AS a_rowid");
		String sqlTableName = componentSqlHelper.getSqlTableName(ed);
		sqlBuilder.FROM(new StringBuilder(sqlTableName).append(" t").toString());

		componentSqlHelper.includeOrderedJoinList(sqlBuilder, joinMap);

		String whereFilters = componentSqlHelper.buildWhereSql(context, filterRootNode);
		if (whereFilters != null && !whereFilters.isEmpty()) {
			sqlBuilder.WHERE(whereFilters);
		}

		componentSqlHelper.orderBy(sqlBuilder, sortOrders, joinMap, ed);

		return sqlBuilder.toString();
	}

	private static class BuildSelectSuggestNestedResult {

		private final String sql;

		private final Map<String, Object> valueFilterMap;

		public BuildSelectSuggestNestedResult(String sql, Map<String, Object> valueFilterMap) {
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

	private class SelectSuggestNestedSqlSource<E extends IComponent> implements SqlSource {

		private final Class<E> componentClass;

		private final Set<String> columns;

		private final SqlSourceBuilder sqlSourceParser;

		public SelectSuggestNestedSqlSource(Class<E> componentClass, Set<String> columns) {
			super();

			this.componentClass = componentClass;
			this.columns = columns;
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
				BuildSelectSuggestNestedResult buildSelectSuggestNestedResult = buildSelectSuggest(componentClass, (RootNode) map.get(FILTER_ROOT_NODE_NAME),
						(List<ISortOrder>) map.get(SORT_ORDERS_NAME), columns);
				map.put(VALUE_FILTER_MAP_NAME, buildSelectSuggestNestedResult.getValueFilterMap());
				String sql = buildSelectSuggestNestedResult.getSql();
				Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
				return sqlSourceParser.parse(sql, parameterType, null);
			} catch (Exception e) {
				throw new BuilderException("Error invoking Select method for SelectSuggestNested Cause: " + e, e);
			}
		}
	}
}
