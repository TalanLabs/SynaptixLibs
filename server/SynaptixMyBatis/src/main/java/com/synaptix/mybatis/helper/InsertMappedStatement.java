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
import com.synaptix.component.factory.ComponentDescriptor.PropertyDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.extension.DatabasePropertyExtensionDescriptor;
import com.synaptix.entity.extension.IDatabaseComponentExtension;
import com.synaptix.mybatis.cache.SynaptixCacheManager;
import com.synaptix.mybatis.hack.SynaptixConfiguration;

public class InsertMappedStatement {

	private static final Log LOG = LogFactory.getLog(InsertMappedStatement.class);

	private final ComponentSqlHelper componentSqlHelper;

	private final SynaptixCacheManager cacheManager;

	@Inject
	private SynaptixConfiguration synaptixConfiguration;

	@Inject
	public InsertMappedStatement(ComponentSqlHelper componentSqlHelper, SynaptixCacheManager cacheManager) {
		super();

		this.componentSqlHelper = componentSqlHelper;
		this.cacheManager = cacheManager;
	}

	public <E extends IEntity> MappedStatement getInsertMappedStatement(Class<E> entityClass) {
		String key = MappedStatementHelper.buildMappedStatementKey(entityClass, null, "insert");

		MappedStatement mappedStatement = null;
		if (!synaptixConfiguration.hasComponentStatement(key)) {
			MappedStatement.Builder msBuilder = new MappedStatement.Builder(synaptixConfiguration, key, new InsertSqlSource<E>(entityClass), SqlCommandType.INSERT);
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
		}
		mappedStatement = synaptixConfiguration.getComponentMappedStatement(key);
		return mappedStatement;
	}

	private <E extends IEntity> String buildInsert(Class<E> entityClass, E entity) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(entityClass);

		SQL sqlBuilder = new SQL();
		sqlBuilder.INSERT_INTO(componentSqlHelper.getSqlTableName(ed));
		for (PropertyDescriptor field : ed.getPropertyDescriptors()) {
			DatabasePropertyExtensionDescriptor dp = (DatabasePropertyExtensionDescriptor) field.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);
			if (dp != null) {
				if (dp.getColumn() != null && dp.getColumn().getSqlName() != null && !dp.getColumn().isReadOnly()) {
					if (entity.straightGetProperty(field.getPropertyName()) != null) {
						// we don't save null values (so that we are subject to get
						// the default value)
						StringBuilder sb = new StringBuilder();
						if (dp.getColumn().isUpperOnly()) {
							sb.append("UPPER(");
						}
						sb.append(componentSqlHelper.getField(field));
						if (dp.getColumn().isUpperOnly()) {
							sb.append(")");
						}
						sqlBuilder.VALUES(dp.getColumn().getSqlName(), sb.toString());
					}
				} else if (dp.getNlsColumn() != null) {
					if (entity.straightGetProperty(field.getPropertyName()) != null) {
						// we don't save null values (so that we are subject to get
						// the default value)
						StringBuilder sb = new StringBuilder();
						if (dp.getNlsColumn().isUpperOnly()) {
							sb.append("UPPER(");
						}
						sb.append(componentSqlHelper.getField(field));
						if (dp.getNlsColumn().isUpperOnly()) {
							sb.append(")");
						}
						sqlBuilder.VALUES(dp.getNlsColumn().getSqlName(), sb.toString());
					}
				}
			}
		}
		String sql = sqlBuilder.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		return sql;
	}

	private class InsertSqlSource<E extends IEntity> implements SqlSource {

		private final Class<E> entityClass;

		private final SqlSourceBuilder sqlSourceParser;

		public InsertSqlSource(Class<E> entityClass) {
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
				String sql = buildInsert(entityClass, (E) parameterObject);
				Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
				return sqlSourceParser.parse(sql, parameterType, null);
			} catch (Exception e) {
				throw new BuilderException("Error invoking Count method for Insert Cause: " + e, e);
			}
		}
	}
}
