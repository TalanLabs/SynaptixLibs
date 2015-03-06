package com.synaptix.mybatis.helper;

import java.util.ArrayList;
import java.util.Arrays;

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
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.IEntity;
import com.synaptix.mybatis.cache.SynaptixCacheManager;
import com.synaptix.mybatis.hack.SynaptixConfiguration;

public class DeleteMappedStatement {

	private static final Log LOG = LogFactory.getLog(DeleteMappedStatement.class);

	private final ComponentSqlHelper componentSqlHelper;

	private final SynaptixCacheManager cacheManager;

	@Inject
	private SynaptixConfiguration synaptixConfiguration;

	@Inject
	public DeleteMappedStatement(ComponentSqlHelper componentSqlHelper, SynaptixCacheManager cacheManager) {
		super();

		this.componentSqlHelper = componentSqlHelper;
		this.cacheManager = cacheManager;
	}

	public <E extends IEntity> MappedStatement getDeleteMappedStatement(Class<E> entityClass) {
		String key = MappedStatementHelper.buildMappedStatementKey(entityClass, null, "delete");

		MappedStatement mappedStatement = null;
		synchronized (entityClass) {
			if (!synaptixConfiguration.hasComponentStatement(key)) {
				MappedStatement.Builder msBuilder = new MappedStatement.Builder(synaptixConfiguration, key, new DeleteSqlSource<E>(entityClass), SqlCommandType.DELETE);
				ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(synaptixConfiguration, key + "-Inline", Integer.class, new ArrayList<ResultMapping>(), null);
				msBuilder.resultMaps(Arrays.asList(inlineResultMapBuilder.build()));
				SynaptixCacheManager.CacheResult cacheResult = cacheManager.getCache(entityClass);
				if (cacheResult != null) {
					msBuilder.flushCacheRequired(true);
					msBuilder.cache(cacheResult.getCache());
					msBuilder.useCache(true);
				}
				mappedStatement = msBuilder.build();
				synaptixConfiguration.addMappedStatement(mappedStatement);
			} else {
				mappedStatement = synaptixConfiguration.getComponentMappedStatement(key);
			}
		}
		return mappedStatement;
	}

	private <E extends IEntity> String buildDelete(Class<E> entityClass, E entity) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(entityClass);

		SQL sqlBuilder = new SQL();
		sqlBuilder.DELETE_FROM(componentSqlHelper.getSqlTableName(ed));
		sqlBuilder.WHERE("id = #{id,javaType=java.io.Serializable}");
		sqlBuilder.WHERE("version = #{version,javaType=java.lang.Integer}");
		String sql = sqlBuilder.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		return sql;
	}

	private class DeleteSqlSource<E extends IEntity> implements SqlSource {

		private final Class<E> entityClass;

		private final SqlSourceBuilder sqlSourceParser;

		public DeleteSqlSource(Class<E> entityClass) {
			super();

			this.entityClass = entityClass;
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
				String sql = buildDelete(entityClass, (E) parameterObject);
				Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
				return sqlSourceParser.parse(sql, parameterType, null);
			} catch (Exception e) {
				throw new BuilderException("Error invoking Count method for Delete Cause: " + e, e);
			}
		}
	}
}
