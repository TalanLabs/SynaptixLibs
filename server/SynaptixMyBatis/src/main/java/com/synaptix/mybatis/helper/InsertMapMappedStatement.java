package com.synaptix.mybatis.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import com.synaptix.entity.IEntity;
import com.synaptix.mybatis.hack.SynaptixConfiguration;

public class InsertMapMappedStatement {

	private static final Log LOG = LogFactory.getLog(InsertMapMappedStatement.class);

	private static final String VALUE_MAP_NAME = "valueMap";

	private static final String TABLE_NAME = "tableName";

	@Inject
	private SynaptixConfiguration synaptixConfiguration;

	@Inject
	public InsertMapMappedStatement() {
		super();
	}

	public Map<String, Object> createInsertMapParameter(String tableName, Map<String, Object> valueMap) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(TABLE_NAME, tableName);
		map.put(VALUE_MAP_NAME, valueMap);
		return map;
	}

	public synchronized MappedStatement getInsertMapMappedStatement() {
		String key = MappedStatementHelper.buildMappedStatementKey(null, null, "insertMap-");

		MappedStatement mappedStatement = null;
		if (!synaptixConfiguration.hasComponentStatement(key)) {
			MappedStatement.Builder msBuilder = new MappedStatement.Builder(synaptixConfiguration, key, new InsertMapSqlSource(), SqlCommandType.INSERT);
			ResultMap.Builder inlineResultMapBuilder = new ResultMap.Builder(synaptixConfiguration, key + "-Inline", Integer.class, new ArrayList<ResultMapping>(), null);
			msBuilder.resultMaps(Arrays.asList(inlineResultMapBuilder.build()));
			mappedStatement = msBuilder.build();
			synaptixConfiguration.addMappedStatement(mappedStatement);
		} else {
			mappedStatement = synaptixConfiguration.getComponentMappedStatement(key);
		}
		return mappedStatement;
	}

	private <E extends IEntity> String buildInsertMap(String tableName, Map<String, Object> valueMap) {
		SQL sqlBuilder = new SQL();
		sqlBuilder.INSERT_INTO(tableName);
		for (Entry<String, Object> entry : valueMap.entrySet()) {
			if (entry.getValue() != null) {
				sqlBuilder.VALUES(entry.getKey(), new StringBuilder("#{").append(VALUE_MAP_NAME).append(".").append(entry.getKey()).append("}").toString());
			}
		}
		String sql = sqlBuilder.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		return sql;
	}

	private class InsertMapSqlSource implements SqlSource {

		private final SqlSourceBuilder sqlSourceParser;

		public InsertMapSqlSource() {
			super();

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
				String sql = buildInsertMap((String) map.get(TABLE_NAME), (Map<String, Object>) map.get(VALUE_MAP_NAME));
				Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
				return sqlSourceParser.parse(sql, parameterType, null);
			} catch (Exception e) {
				throw new BuilderException("Error invoking Count method for Insert Map Cause: " + e, e);
			}
		}
	}
}
