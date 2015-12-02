package com.synaptix.mybatis.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

public class SelectNlsMappedStatement {

	private static final Log LOG = LogFactory.getLog(PaginationOldMappedStatement.class);

	@Inject
	private ComponentSqlHelper componentSqlHelper;

	@Inject
	private SynaptixConfiguration synaptixConfiguration;

	@Inject
	private SynaptixCacheManager cacheManager;

	@Inject
	public SelectNlsMappedStatement() {
		super();
	}

	public <E extends IComponent> MappedStatement getSelectNlsMappedStatement(Class<E> componentClass, String columnName) {
		Set<String> columns = new HashSet<String>(1);
		columns.add(columnName);
		String key = MappedStatementHelper.buildMappedStatementKey(componentClass, columns, "nlsSelect");

		MappedStatement mappedStatement = null;
		if (!synaptixConfiguration.hasComponentStatement(key)) {
			MappedStatement.Builder msBuilder = new MappedStatement.Builder(synaptixConfiguration, key, new SelectNlsSqlSource<E>(componentClass, columnName), SqlCommandType.SELECT);
			ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(synaptixConfiguration, key + "-Inline", String.class, new ArrayList<ResultMapping>(), null);
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

	private <E extends IComponent> String buildSelectNls(Class<E> componentClass, String columnName) {
		ComponentDescriptor ed = ComponentFactory.getInstance().getDescriptor(componentClass);

		SQL sqlBuilder = new SQL();
		String tableName = componentSqlHelper.getSqlTableName(ed);
		StringBuilder functionSb = new StringBuilder("P_NLS.get_nls_server_message('");
		functionSb.append(tableName).append("',#{id,javaType=IId},'").append(columnName).append("',#{defaultMeaning,javaType=String,jdbcType=VARCHAR})");
		sqlBuilder.SELECT(functionSb.toString());
		sqlBuilder.FROM("dual");

		String sql = sqlBuilder.toString();

		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}

		return sql;
	}

	private class SelectNlsSqlSource<E extends IComponent> implements SqlSource {

		private final SqlSource sqlSource;

		public SelectNlsSqlSource(Class<E> componentClass, String columnName) {
			super();

			SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(synaptixConfiguration);

			String sql = buildSelectNls(componentClass, columnName);
			this.sqlSource = sqlSourceParser.parse(sql, Map.class, null);
		}

		@Override
		public BoundSql getBoundSql(Object parameterObject) {
			return sqlSource.getBoundSql(parameterObject);
		}
	}
}
