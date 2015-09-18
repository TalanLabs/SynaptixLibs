package com.synaptix.mybatis.helper;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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
import com.synaptix.mybatis.hack.SynaptixConfiguration;
import com.synaptix.mybatis.helper.ComponentSqlHelper.Join;
import com.synaptix.service.helper.SortOrderHelper;
import com.synaptix.service.model.ISortOrder;

public class PaginationOldMappedStatement {

	private static final Log LOG = LogFactory.getLog(PaginationOldMappedStatement.class);

	private static final String VALUE_FILTER_MAP_NAME = "valueFilterMap";

	private static final String TO_NAME = "to";

	private static final String FROM_NAME = "from";

	private static final String SORT_ORDERS_NAME = "sortOrders";

	@Inject
	private ComponentSqlHelper componentSqlHelper;

	@Inject
	private SynaptixCacheManager cacheManager;

	@Inject
	private ComponentResultMapHelper componentResultMapHelper;

	@Inject
	private SynaptixConfiguration synaptixConfiguration;

	private Map<PaginationKey, SoftReference<String>> countSqlMap;

	private Map<PaginationKey, SoftReference<String>> paginationSqlMap;

	@Inject
	public PaginationOldMappedStatement() {
		super();

		this.countSqlMap = new HashMap<PaginationKey, SoftReference<String>>();
		this.paginationSqlMap = new HashMap<PaginationKey, SoftReference<String>>();
	}

	/**
	 * Create count pagination parameter
	 *
	 * @param componentClass
	 * @param valueFilterMap
	 * @return
	 */
	public <E extends IComponent> Map<String, Object> createCountPaginationParameterOld(Class<E> componentClass, Map<String, Object> valueFilterMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(VALUE_FILTER_MAP_NAME, valueFilterMap);
		return map;
	}

	/**
	 * Get count pagination mapped statement
	 *
	 * @param componentClass
	 * @return
	 */
	public <E extends IComponent> MappedStatement getCountPaginationMappedStatementOld(Class<E> componentClass) {
		String key = MappedStatementHelper.buildMappedStatementKey(componentClass, null, "countPaginationOld");

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

	/**
	 * Create select pagination parameter
	 *
	 * @param componentClass
	 * @param valueFilterMap
	 * @param from
	 * @param to
	 * @param sortOrders
	 * @param columns
	 * @return
	 */
	public <E extends IComponent> Map<String, Object> createSelectPaginationParameterOld(Class<E> componentClass, Map<String, Object> valueFilterMap, int from, int to, List<ISortOrder> sortOrders) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(VALUE_FILTER_MAP_NAME, valueFilterMap);
		map.put(FROM_NAME, from);
		map.put(TO_NAME, to);
		map.put(SORT_ORDERS_NAME, sortOrders);
		return map;
	}

	/**
	 * Get select pagination mapped statement
	 *
	 * @param componentClass
	 * @param columns
	 * @param maxRow
	 * @return
	 */
	public <E extends IComponent> MappedStatement getSelectPaginationMappedStatementOld(Class<E> componentClass, Set<String> columns, int maxRow) {
		Set<String> cs = new HashSet<String>(columns);
		cs.add("maxRow-" + maxRow);
		String key = MappedStatementHelper.buildMappedStatementKey(componentClass, cs, "selectPaginationOld");

		MappedStatement mappedStatement = null;
		if (!synaptixConfiguration.hasComponentStatement(key)) {
			ResultMap inlineResultMap = componentResultMapHelper.getNestedResultMap(componentClass, columns);

			MappedStatement.Builder msBuilder = new MappedStatement.Builder(synaptixConfiguration, key, new SelectPaginationNestedSqlSource<E>(componentClass, columns), SqlCommandType.SELECT);
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
	private <E extends IComponent> BuildCountPaginationOldResult buildCountPaginationOld(Class<E> componentClass, Map<String, Object> valueFilterMap) {
		String sql = null;

		if (componentSqlHelper.isUseSqlCache(valueFilterMap)) {
			PaginationKey key = new PaginationKey(componentClass, componentSqlHelper.getCleanFilters(valueFilterMap), null, null);
			synchronized (componentClass) {
				SoftReference<String> wr = countSqlMap.get(key);
				sql = wr != null ? wr.get() : null;
				if (sql == null) {
					sql = _buildCountPaginationOld(componentClass, valueFilterMap);
					countSqlMap.put(key, new SoftReference<String>(sql));
				}
			}
		} else {
			sql = _buildCountPaginationOld(componentClass, valueFilterMap);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}

		return new BuildCountPaginationOldResult(sql, componentSqlHelper.transformValueFilterMap(componentClass, valueFilterMap));
	}

	private <E extends IComponent> String _buildCountPaginationOld(Class<E> componentClass, Map<String, Object> valueFilterMap) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);

		Map<String, ComponentSqlHelper.Join> joinMap = new HashMap<String, ComponentSqlHelper.Join>();
		componentSqlHelper.buildFilterJoinMap(componentSqlHelper.getCleanFilters(valueFilterMap), joinMap, ed, null, "");

		SQL sqlBuilder = new SQL();
		sqlBuilder.SELECT("count(0)");
		sqlBuilder.FROM(new StringBuilder(componentSqlHelper.getSqlTableName(ed)).append(" t").toString());

		componentSqlHelper.includeOrderedJoinList(sqlBuilder, joinMap);

		if (componentSqlHelper.isNotEmptyValueFilterMap(valueFilterMap)) {
			String whereFilters = componentSqlHelper.whereFilters("valueFilterMap", "AND", joinMap, ed, valueFilterMap);
			if (whereFilters != null && !whereFilters.isEmpty()) {
				sqlBuilder.WHERE(whereFilters);
			}
		}

		return sqlBuilder.toString();
	}

	private static class BuildCountPaginationOldResult {

		private final String sql;

		private final Map<String, Object> valueFilterMap;

		public BuildCountPaginationOldResult(String sql, Map<String, Object> valueFilterMap) {
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
	private <E extends IComponent> BuildSelectPaginationNestedOldResult buildSelectPaginationOld(Class<E> componentClass, Map<String, Object> valueFilterMap, List<ISortOrder> sortOrders,
			Set<String> columns) {
		String sql = null;

		if (componentSqlHelper.isUseSqlCache(valueFilterMap)) {
			PaginationKey key = new PaginationKey(componentClass, componentSqlHelper.getCleanFilters(valueFilterMap), sortOrders, columns);
			synchronized (componentClass) {
				SoftReference<String> wr = paginationSqlMap.get(key);
				sql = wr != null ? wr.get() : null;
				if (sql == null) {
					sql = _buildSelectPaginationOld(componentClass, valueFilterMap, sortOrders, columns);
					paginationSqlMap.put(key, new SoftReference<String>(sql));
				}
			}
		} else {
			sql = _buildSelectPaginationOld(componentClass, valueFilterMap, sortOrders, columns);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}

		return new BuildSelectPaginationNestedOldResult(sql, componentSqlHelper.transformValueFilterMap(componentClass, valueFilterMap));
	}

	private <E extends IComponent> String _buildSelectPaginationOld(Class<E> componentClass, Map<String, Object> valueFilterMap, List<ISortOrder> sortOrders, Set<String> columns) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);

		Map<String, Join> joinMap = new HashMap<String, Join>();
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
		sqlBuilder.FROM(new StringBuilder("(SELECT t.* FROM (SELECT t.*, ROWNUM AS rn FROM (").append(buildFirstSelect(componentClass, valueFilterMap, sortOrders))
				.append(") t WHERE #{to,javaType=int} >= ROWNUM) t WHERE rn >= #{from,javaType=int}) i, ").append(sqlTableName).append(" t").toString());
		componentSqlHelper.includeOrderedJoinList(sqlBuilder, joinMap);
		sqlBuilder.WHERE("(i.a_rowid = t.ROWID)");
		sqlBuilder.ORDER_BY("i.rn");

		return sqlBuilder.toString();
	}

	private <E extends IComponent> String buildFirstSelect(Class<E> componentClass, Map<String, Object> valueFilterMap, List<ISortOrder> sortOrders) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);

		Map<String, Join> joinMap = new HashMap<String, Join>();
		if (sortOrders != null && !sortOrders.isEmpty()) {
			componentSqlHelper.buildSelectJoinMap(joinMap, ed, null, "", SortOrderHelper.getInstance().extractColumns(sortOrders), false);
		}
		componentSqlHelper.buildFilterJoinMap(componentSqlHelper.getCleanFilters(valueFilterMap), joinMap, ed, null, "");
		componentSqlHelper.buildOrderJoinMap(sortOrders, joinMap, ed, null, "");

		SQL sqlBuilder = new SQL();
		String hint = componentSqlHelper.buildHint(ed, valueFilterMap);
		sqlBuilder.SELECT(hint + " t.ROWID AS a_rowid");
		String sqlTableName = componentSqlHelper.getSqlTableName(ed);
		sqlBuilder.FROM(new StringBuilder(sqlTableName).append(" t").toString());

		componentSqlHelper.includeOrderedJoinList(sqlBuilder, joinMap);

		if (componentSqlHelper.isNotEmptyValueFilterMap(valueFilterMap)) {
			String whereFilters = componentSqlHelper.whereFilters("valueFilterMap", "AND", joinMap, ed, valueFilterMap);
			if (whereFilters != null && !whereFilters.isEmpty()) {
				sqlBuilder.WHERE(whereFilters);
			}
		}

		componentSqlHelper.orderBy(sqlBuilder, sortOrders, joinMap, ed);

		return sqlBuilder.toString();
	}

	private static class BuildSelectPaginationNestedOldResult {

		private final String sql;

		private final Map<String, Object> valueFilterMap;

		public BuildSelectPaginationNestedOldResult(String sql, Map<String, Object> valueFilterMap) {
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
				BuildCountPaginationOldResult buildCountPaginationOldResult = buildCountPaginationOld(componentClass, (Map<String, Object>) map.get(VALUE_FILTER_MAP_NAME));
				map.put(VALUE_FILTER_MAP_NAME, buildCountPaginationOldResult.getValueFilterMap());
				String sql = buildCountPaginationOldResult.getSql();
				Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
				return sqlSourceParser.parse(sql, parameterType, null);
			} catch (Exception e) {
				throw new BuilderException("Error invoking Count method for CountPagination Cause: " + e, e);
			}
		}
	}

	private class SelectPaginationNestedSqlSource<E extends IComponent> implements SqlSource {

		private final Class<E> componentClass;

		private final Set<String> columns;

		private final SqlSourceBuilder sqlSourceParser;

		public SelectPaginationNestedSqlSource(Class<E> componentClass, Set<String> columns) {
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
				BuildSelectPaginationNestedOldResult buildSelectPaginationNestedOldResult = buildSelectPaginationOld(componentClass, (Map<String, Object>) map.get(VALUE_FILTER_MAP_NAME),
						(List<ISortOrder>) map.get(SORT_ORDERS_NAME), columns);
				map.put(VALUE_FILTER_MAP_NAME, buildSelectPaginationNestedOldResult.getValueFilterMap());
				String sql = buildSelectPaginationNestedOldResult.getSql();
				Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
				return sqlSourceParser.parse(sql, parameterType, null);
			} catch (Exception e) {
				throw new BuilderException("Error invoking Select method for SelectPaginationNested Cause: " + e, e);
			}
		}
	}

	private static class PaginationKey {

		private final Class<? extends IComponent> componentClass;

		private final Set<String> filterColumns;

		private final List<ISortOrder> sortOrders;

		private final Set<String> columns;

		public PaginationKey(Class<? extends IComponent> componentClass, Set<String> filterColumns, List<ISortOrder> sortOrders, Set<String> columns) {
			super();
			this.componentClass = componentClass;
			this.filterColumns = filterColumns;
			this.sortOrders = sortOrders;
			this.columns = columns;
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder().append(componentClass).append(filterColumns).append(sortOrders).append(columns).toHashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof PaginationKey) {
				PaginationKey o = (PaginationKey) obj;
				return new EqualsBuilder().append(componentClass, o.componentClass).append(filterColumns, o.filterColumns).append(sortOrders, o.sortOrders).append(columns, o.columns).isEquals();
			}
			return super.equals(obj);
		}
	}
}
