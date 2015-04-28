package com.synaptix.mybatis.helper;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;

import com.google.inject.Inject;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.IComponent;
import com.synaptix.mybatis.cache.SynaptixCacheManager;
import com.synaptix.mybatis.hack.SynaptixConfiguration;

public class SelectSqlMappedStatement {

	private static final Log LOG = LogFactory.getLog(SelectSqlMappedStatement.class);

	private final SynaptixCacheManager cacheManager;

	private SynaptixConfiguration synaptixConfiguration;

	private ComponentResultMapHelper componentResultMapHelper;

	@Inject
	public SelectSqlMappedStatement(SynaptixCacheManager cacheManager) {
		super();

		this.cacheManager = cacheManager;
	}

	@Inject
	public void setSynatixConfiguration(SynaptixConfiguration synaptixConfiguration) {
		this.synaptixConfiguration = synaptixConfiguration;
	}

	@Inject
	public void setComponentResultMapHelper(ComponentResultMapHelper componentResultMapHelper) {
		this.componentResultMapHelper = componentResultMapHelper;
	}

	/**
	 * Get a select list mapped statement, if not exists, create a mapped and result
	 * 
	 * @param componentClass
	 * @return
	 */
	public <E extends IComponent> MappedStatement getSelectSqlMappedStatement(Class<E> componentClass, String sql) {
		String key = MappedStatementHelper.buildMappedStatementKey(componentClass, null, "selectSql-" + sql);

		MappedStatement mappedStatement = null;
		if (!synaptixConfiguration.hasComponentStatement(key)) {
			ResultMap inlineResultMap = componentResultMapHelper.getResultMap(componentClass);

			MappedStatement.Builder msBuilder = new MappedStatement.Builder(synaptixConfiguration, key, new SelectSqlSource(sql), SqlCommandType.SELECT);
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
	 * Get a select list mapped statement, if not exists, create a mapped and result
	 * 
	 * @param componentClass
	 * @param typeClass
	 * @param sql
	 * @return
	 */
	public <E extends IComponent> MappedStatement getSelectSqlMappedStatement(Class<E> componentClass, Class<?> typeClass, String sql) {
		String key = MappedStatementHelper.buildMappedStatementKey(componentClass, CollectionHelper.asSet(typeClass.getName()), "selectSql-" + sql);

		MappedStatement mappedStatement = null;
		if (!synaptixConfiguration.hasComponentStatement(key)) {
			ResultMap inlineResultMap = componentResultMapHelper.getResultMap(typeClass);

			MappedStatement.Builder msBuilder = new MappedStatement.Builder(synaptixConfiguration, key, new SelectSqlSource(sql), SqlCommandType.SELECT);
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

	private class SelectSqlSource implements SqlSource {

		private final String sql;

		private final SqlSourceBuilder sqlSourceParser;

		public SelectSqlSource(String sql) {
			super();

			this.sql = sql;
			this.sqlSourceParser = new SqlSourceBuilder(synaptixConfiguration);
		}

		@Override
		public BoundSql getBoundSql(Object parameterObject) {
			SqlSource sqlSource = createSqlSource(parameterObject);
			return sqlSource.getBoundSql(parameterObject);
		}

		private SqlSource createSqlSource(Object parameterObject) {
			try {
				Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
				return sqlSourceParser.parse(sql, parameterType, null);
			} catch (Exception e) {
				throw new BuilderException("Error invoking Select method for SelectList Cause: " + e, e);
			}
		}
	}
}
