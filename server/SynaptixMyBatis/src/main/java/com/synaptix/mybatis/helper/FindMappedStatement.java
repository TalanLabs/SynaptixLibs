package com.synaptix.mybatis.helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;

import com.google.inject.Inject;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentDescriptor.PropertyDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.EntityFields;
import com.synaptix.entity.ICancellable;
import com.synaptix.entity.IId;
import com.synaptix.entity.extension.DatabasePropertyExtensionDescriptor;
import com.synaptix.entity.extension.IDatabaseComponentExtension;
import com.synaptix.mybatis.cache.SynaptixCacheManager;
import com.synaptix.mybatis.hack.SynaptixConfiguration;
import com.synaptix.mybatis.helper.ComponentSqlHelper.Join;
import com.synaptix.service.ServiceException;

public class FindMappedStatement {

	private static final Log LOG = LogFactory.getLog(FindMappedStatement.class);

	private static final String PAGINATION_ERROR = "paginationError";

	private final ComponentSqlHelper componentSqlHelper;

	private final ComponentColumnsCache componentColumnsCache;

	private final SynaptixCacheManager cacheManager;

	private SynaptixConfiguration synaptixConfiguration;

	private ComponentResultMapHelper componentResultMapHelper;

	@Inject
	public FindMappedStatement(ComponentSqlHelper componentSqlHelper, ComponentColumnsCache componentColumnsCache, SynaptixCacheManager cacheManager) {
		super();

		this.componentSqlHelper = componentSqlHelper;
		this.componentColumnsCache = componentColumnsCache;
		this.cacheManager = cacheManager;
	}

	@Inject
	public void setComponentResultMapHelper(ComponentResultMapHelper componentResultMapHelper) {
		this.componentResultMapHelper = componentResultMapHelper;
	}

	@Inject
	public void setSynaptixConfiguration(SynaptixConfiguration synaptixConfiguration) {
		this.synaptixConfiguration = synaptixConfiguration;
	}

	public <E extends IComponent> MappedStatement createFindEntityById(Class<E> componentClass) {
		String key = new StringBuilder().append(componentClass.getName()).append("/findEntityById").toString();
		MappedStatement mappedStatement = null;
		if (!synaptixConfiguration.hasComponentStatement(key)) {
			ResultMap inlineResultMap = componentResultMapHelper.getResultMapWithNested(componentClass);

			MappedStatement.Builder msBuilder = new MappedStatement.Builder(synaptixConfiguration, key, new FindComponentsByPropertyNameSqlSource<E>(componentClass, EntityFields.id().name(), false),
					SqlCommandType.SELECT);
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

	public <E extends IComponent> MappedStatement createFindChildrenByIdParent(Class<E> componentClass, String idParentPropertyName) {
		String key = new StringBuilder().append(componentClass.getName()).append("/findChildrenByIdParent?").append(idParentPropertyName).toString();
		MappedStatement mappedStatement = null;
		if (!synaptixConfiguration.hasComponentStatement(key)) {
			ResultMap inlineResultMap = componentResultMapHelper.getResultMapWithNested(componentClass);

			MappedStatement.Builder msBuilder = new MappedStatement.Builder(synaptixConfiguration, key, new FindComponentsByPropertyNameSqlSource<E>(componentClass, idParentPropertyName, true),
					SqlCommandType.SELECT);
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

	public <E extends IComponent> MappedStatement getFindComponentsByPropertyNameMappedStatement(Class<E> componentClass, String propertyName, boolean useCheckCancel) {
		String key = MappedStatementHelper.buildMappedStatementKey(componentClass, CollectionHelper.asSet(propertyName, String.valueOf(useCheckCancel)), "findComponentsByPropertyName");

		MappedStatement mappedStatement = null;
		if (!synaptixConfiguration.hasComponentStatement(key)) {
			ResultMap inlineResultMap = componentResultMapHelper.getResultMapWithNested(componentClass);

			MappedStatement.Builder msBuilder = new MappedStatement.Builder(synaptixConfiguration, key, new FindComponentsByPropertyNameSqlSource<E>(componentClass, propertyName, useCheckCancel),
					SqlCommandType.SELECT);
			msBuilder.resultMaps(Arrays.asList(inlineResultMap));
			SynaptixCacheManager.CacheResult cacheResult = cacheManager.getCache(componentClass);
			if (cacheResult != null && cacheResult.isEnabled()) {
				msBuilder.flushCacheRequired(false);
				msBuilder.cache(cacheResult.getCache());
				msBuilder.useCache(true);
			}
			mappedStatement = msBuilder.build();
			synchronized (componentClass) {
				if (!synaptixConfiguration.hasComponentStatement(key)) {
					synaptixConfiguration.addMappedStatement(mappedStatement);
				}
			}
		}
		mappedStatement = synaptixConfiguration.getComponentMappedStatement(key);
		return mappedStatement;
	}

	public <E extends IComponent> MappedStatement getFindComponentsByPropertyNameMappedStatement(Class<E> componentClass, String idTarget, String propertyName, boolean useCheckCancel) {
		String key = MappedStatementHelper.buildMappedStatementKey(componentClass, CollectionHelper.asSet(propertyName, idTarget, String.valueOf(useCheckCancel)), "findComponentsByPropertyName");

		MappedStatement mappedStatement = null;
		if (!synaptixConfiguration.hasComponentStatement(key)) {
			ResultMap inlineResultMap = componentResultMapHelper.getResultMapWithNested(componentClass);

			MappedStatement.Builder msBuilder = new MappedStatement.Builder(synaptixConfiguration, key, new FindComponentsByPropertyNameSqlSource<E>(componentClass, null, idTarget, propertyName,
					useCheckCancel), SqlCommandType.SELECT);
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

	public <E extends IComponent> MappedStatement getFindComponentsByPropertyNameMappedStatement(Class<E> componentClass, String assoSqlTableName, String idSource, String idTarget,
			String propertyName, boolean useCheckCancel) {
		String key = MappedStatementHelper.buildMappedStatementKey(componentClass, CollectionHelper.asSet(assoSqlTableName, idSource, idTarget, propertyName, String.valueOf(useCheckCancel)),
				"findComponentsByAssoPropertyName");

		MappedStatement mappedStatement = null;
		if (!synaptixConfiguration.hasComponentStatement(key)) {
			ResultMap inlineResultMap = componentResultMapHelper.getResultMapWithNested(componentClass);

			MappedStatement.Builder msBuilder = new MappedStatement.Builder(synaptixConfiguration, key, new FindComponentsByPropertyNameSqlSource<E>(componentClass, assoSqlTableName, idSource,
					idTarget, propertyName, useCheckCancel), SqlCommandType.SELECT);
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

	/**
	 * Build a find components by property name
	 *
	 * @param componentClass
	 * @param propertyName
	 * @param useCheckCancel
	 * @return
	 */
	private <E extends IComponent> String buildFindComponentsByPropertyName(Class<E> componentClass, String propertyName, boolean useCheckCancel) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);
		if (!ed.getPropertyNames().contains(propertyName)) {
			throw new ServiceException(PAGINATION_ERROR, "buildFindComponentByIdPropertyName" + " in " + ed.getComponentClass().getSimpleName() + " is not a property " + propertyName, null);
		}

		PropertyDescriptor pd = ed.getPropertyDescriptor(propertyName);
		DatabasePropertyExtensionDescriptor db = (DatabasePropertyExtensionDescriptor) pd.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);
		if (db == null || db.getColumn() == null) {
			if (db == null) {
				LOG.error("db is null");
			}
			throw new ServiceException(PAGINATION_ERROR, "buildFindComponentByIdPropertyName" + " in " + ed.getComponentClass().getSimpleName() + " is not a column for property " + propertyName, null);
		}

		return buildFindComponentsByPropertyName(componentClass, db.getColumn().getSqlName(), propertyName, useCheckCancel);
	}

	private <E extends IComponent> String buildFindComponentsByPropertyName(Class<E> componentClass, String column, String propertyName, boolean useCheckCancel) {
		if (componentColumnsCache.isValid(componentClass)) {
			return buildNestedFindComponentsByPropertyName(componentClass, column, propertyName, useCheckCancel);
		} else {
			return buildSimpleFindComponentsByPropertyName(componentClass, column, propertyName, useCheckCancel);
		}
	}

	private <E extends IComponent> String buildSimpleFindComponentsByPropertyName(Class<E> componentClass, String column, String propertyName, boolean useCheckCancel) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);

		String sqlTableName = componentSqlHelper.getSqlTableName(ed);

		SQL sqlBuilder = new SQL();
		sqlBuilder.SELECT("t.*");
		sqlBuilder.FROM(new StringBuilder(sqlTableName).append(" t").toString());
		sqlBuilder.WHERE(new StringBuilder("t.").append(column).append(" = ").append("#{").append(propertyName).append("}").toString());
		if (useCheckCancel && ICancellable.class.isAssignableFrom(componentClass)) {
			sqlBuilder.AND();
			sqlBuilder.WHERE("t.check_cancel = '0'");
		}
		String sql = sqlBuilder.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		return sql;
	}

	private <E extends IComponent> String buildNestedFindComponentsByPropertyName(Class<E> componentClass, String column, String propertyName, boolean useCheckCancel) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);
		Map<String, Join> joinMap = new HashMap<String, Join>();

		Set<String> columns = componentColumnsCache.getPropertyNames(componentClass);

		componentSqlHelper.buildSelectJoinMap(joinMap, ed, null, "", columns, false);

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
		sqlBuilder.FROM(new StringBuilder(sqlTableName).append(" t").toString());

		componentSqlHelper.includeOrderedJoinList(sqlBuilder, joinMap);

		sqlBuilder.WHERE(new StringBuilder("t.").append(column).append(" = ").append("#{").append(propertyName).append("}").toString());
		if (useCheckCancel && ICancellable.class.isAssignableFrom(componentClass)) {
			sqlBuilder.AND();
			sqlBuilder.WHERE("t.check_cancel = '0'");
		}
		String sql = sqlBuilder.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		return sql;
	}

	private <E extends IComponent> String buildFindComponentsByPropertyName(Class<E> componentClass, String assoSqlTableName, String idSource, String idTarget, String propertyName,
			boolean useCheckCancel) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);
		String sqlTableName = componentSqlHelper.getSqlTableName(ed);
		if (sqlTableName.equals(assoSqlTableName)) {
			return buildFindComponentsByPropertyName(componentClass, idTarget, propertyName, useCheckCancel);
		}

		if (componentColumnsCache.isValid(componentClass)) {
			return buildNestedFindComponentsByPropertyName(componentClass, assoSqlTableName, idSource, idTarget, propertyName, useCheckCancel);
		} else {
			return buildSimpleFindComponentsByPropertyName(componentClass, assoSqlTableName, idSource, idTarget, propertyName, useCheckCancel);
		}
	}

	private <E extends IComponent> String buildSimpleFindComponentsByPropertyName(Class<E> componentClass, String assoSqlTableName, String idSource, String idTarget, String propertyName,
			boolean useCheckCancel) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);

		SQL sqlBuilder = new SQL();
		sqlBuilder.SELECT("t.*");
		String sqlTableName = componentSqlHelper.getSqlTableName(ed);
		sqlBuilder.FROM(new StringBuilder(sqlTableName).append(" t").toString());
		sqlBuilder.INNER_JOIN(new StringBuilder(assoSqlTableName).append(" a").append(" on a.").append(idTarget).append(" = t.ID").toString());
		sqlBuilder.WHERE(new StringBuilder("a.").append(idSource).append(" = ").append("#{").append(propertyName).append("}").toString());
		if (useCheckCancel && ICancellable.class.isAssignableFrom(componentClass)) {
			sqlBuilder.AND();
			sqlBuilder.WHERE("t.check_cancel = '0'");
		}
		String sql = sqlBuilder.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		return sql;
	}

	private <E extends IComponent> String buildNestedFindComponentsByPropertyName(Class<E> componentClass, String assoSqlTableName, String idSource, String idTarget, String propertyName,
			boolean useCheckCancel) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);
		Map<String, Join> joinMap = new HashMap<String, Join>();

		Set<String> columns = componentColumnsCache.getPropertyNames(componentClass);

		componentSqlHelper.buildSelectJoinMap(joinMap, ed, null, "", columns, false);

		SQL sqlBuilder = new SQL();
		componentSqlHelper.selectFields(sqlBuilder, joinMap, ed, null, null, columns, null);
		String sqlTableName = componentSqlHelper.getSqlTableName(ed);
		sqlBuilder.FROM(new StringBuilder(sqlTableName).append(" t").toString());
		sqlBuilder.INNER_JOIN(new StringBuilder(assoSqlTableName).append(" a").append(" on a.").append(idTarget).append(" = t.ID").toString());
		componentSqlHelper.includeOrderedJoinList(sqlBuilder, joinMap);

		sqlBuilder.WHERE(new StringBuilder("a.").append(idSource).append(" = ").append("#{").append(propertyName).append("}").toString());
		if (useCheckCancel && ICancellable.class.isAssignableFrom(componentClass)) {
			sqlBuilder.AND();
			sqlBuilder.WHERE("t.check_cancel = '0'");
		}
		String sql = sqlBuilder.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		return sql;
	}

	private class FindComponentsByPropertyNameSqlSource<E extends IComponent> implements SqlSource {

		private final SqlSource sqlSource;

		public FindComponentsByPropertyNameSqlSource(Class<E> componentClass, String propertyName, boolean useCheckCancel) {
			this(componentClass, null, propertyName, useCheckCancel);
		}

		public FindComponentsByPropertyNameSqlSource(Class<E> componentClass, String idSource, String propertyName, boolean useCheckCancel) {
			this(componentClass, null, idSource, null, propertyName, useCheckCancel);
		}

		public FindComponentsByPropertyNameSqlSource(Class<E> componentClass, String idSource, String idTarget, String propertyName, boolean useCheckCancel) {
			this(componentClass, null, idSource, idTarget, propertyName, useCheckCancel);
		}

		public FindComponentsByPropertyNameSqlSource(Class<E> componentClass, String assoSqlTableName, String idSource, String idTarget, String propertyName, boolean useCheckCancel) {
			super();

			SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(synaptixConfiguration);

			String sql;
			if (idTarget == null) {
				sql = buildFindComponentsByPropertyName(componentClass, propertyName, useCheckCancel);
			} else if (assoSqlTableName == null) {
				sql = buildFindComponentsByPropertyName(componentClass, idTarget, propertyName, useCheckCancel);
			} else {
				sql = buildFindComponentsByPropertyName(componentClass, assoSqlTableName, idSource, idTarget, propertyName, useCheckCancel);
			}
			sqlSource = sqlSourceParser.parse(sql, IId.class, null);
		}

		@Override
		public BoundSql getBoundSql(Object parameterObject) {
			return sqlSource.getBoundSql(parameterObject);
		}
	}
}
