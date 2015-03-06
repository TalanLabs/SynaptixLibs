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
import com.synaptix.entity.EntityFields;
import com.synaptix.entity.IEntity;
import com.synaptix.entity.extension.DatabasePropertyExtensionDescriptor;
import com.synaptix.entity.extension.IDatabaseComponentExtension;
import com.synaptix.mybatis.cache.SynaptixCacheManager;
import com.synaptix.mybatis.hack.SynaptixConfiguration;

public class UpdateMappedStatement {

	private static final Log LOG = LogFactory.getLog(UpdateMappedStatement.class);

	private final ComponentSqlHelper componentSqlHelper;

	private final SynaptixCacheManager cacheManager;

	private SynaptixConfiguration synaptixConfiguration;

	@Inject
	public UpdateMappedStatement(ComponentSqlHelper componentSqlHelper, SynaptixCacheManager cacheManager) {
		super();

		this.componentSqlHelper = componentSqlHelper;
		this.cacheManager = cacheManager;
	}

	@Inject
	public void setSynaptixConfiguration(SynaptixConfiguration synaptixConfiguration) {
		this.synaptixConfiguration = synaptixConfiguration;
	}

	public <E extends IEntity> MappedStatement getUpdateMappedStatement(Class<E> entityClass, boolean updateNlsColumn) {
		String key = MappedStatementHelper.buildMappedStatementKey(entityClass, null, new StringBuilder("update-").append(updateNlsColumn).toString());

		MappedStatement mappedStatement = null;
		synchronized (entityClass) {
			if (!synaptixConfiguration.hasComponentStatement(key)) {
				MappedStatement.Builder msBuilder = new MappedStatement.Builder(synaptixConfiguration, key, new UpdateSqlSource<E>(entityClass, updateNlsColumn), SqlCommandType.UPDATE);
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

	private <E extends IEntity> String buildUpdate(Class<E> entityClass, E entity, boolean updateNlsColumn) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(entityClass);

		SQL sqlBuilder = new SQL();
		sqlBuilder.UPDATE(componentSqlHelper.getSqlTableName(ed));
		for (PropertyDescriptor field : ed.getPropertyDescriptors()) {
			DatabasePropertyExtensionDescriptor dp = (DatabasePropertyExtensionDescriptor) field.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);
			if (dp != null) {
				if (dp.getColumn() != null) {
					if (!field.getPropertyName().equals(EntityFields.id().name()) && !field.getPropertyName().equals(EntityFields.version().name()) && dp.getColumn().getSqlName() != null
							&& !dp.getColumn().isReadOnly() && (!dp.getColumn().isNotNull() || entity.straightGetProperty(field.getPropertyName()) != null)) {
						StringBuilder sb = new StringBuilder(dp.getColumn().getSqlName());
						sb.append(" = ");
						if (dp.getColumn().isUpperOnly()) {
							sb.append("UPPER(");
						}
						sb.append(componentSqlHelper.getField(field));
						if (dp.getColumn().isUpperOnly()) {
							sb.append(")");
						}

						sqlBuilder.SET(sb.toString());
					}
				} else if (dp.getNlsColumn() != null) {
					if (updateNlsColumn) {
						if ((!dp.getNlsColumn().isNotNull() || entity.straightGetProperty(field.getPropertyName()) != null)) {
							StringBuilder sb = new StringBuilder(dp.getNlsColumn().getSqlName());
							sb.append(" = ");
							if (dp.getNlsColumn().isUpperOnly()) {
								sb.append("UPPER(");
							}
							sb.append(componentSqlHelper.getField(field));
							if (dp.getNlsColumn().isUpperOnly()) {
								sb.append(")");
							}

							sqlBuilder.SET(sb.toString());
						}
					}
				}
			}
		}
		sqlBuilder.SET("VERSION = #{version,javaType=java.lang.Integer} + 1");
		sqlBuilder.WHERE("ID = #{id,javaType=java.io.Serializable}");
		sqlBuilder.WHERE("VERSION = #{version,javaType=java.lang.Integer}");
		String sql = sqlBuilder.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		return sql;
	}

	private class UpdateSqlSource<E extends IEntity> implements SqlSource {

		private final Class<E> entityClass;

		private final boolean updateNlsColumn;

		private final SqlSourceBuilder sqlSourceParser;

		public UpdateSqlSource(Class<E> entityClass, boolean updateNlsColumn) {
			super();

			this.entityClass = entityClass;
			this.updateNlsColumn = updateNlsColumn;
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
				String sql = buildUpdate(entityClass, (E) parameterObject, updateNlsColumn);
				Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
				return sqlSourceParser.parse(sql, parameterType, null);
			} catch (Exception e) {
				throw new BuilderException("Error invoking Count method for Update Cause: " + e, e);
			}
		}
	}
}
