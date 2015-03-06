package com.synaptix.mybatis.delegate;

import java.util.Map;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.SqlSessionManager;

import com.google.inject.Inject;
import com.synaptix.mybatis.helper.InsertMapMappedStatement;

public class ObjectServiceDelegate {

	@Inject
	private SqlSessionManager sqlSessionManager;

	@Inject
	private InsertMapMappedStatement insertMapMappedStatement;

	/**
	 * Insert a map into tableName
	 * 
	 * @param tableName
	 * @param valueMap
	 * @return
	 */
	public int insertMap(String tableName, Map<String, Object> valueMap) {
		MappedStatement mappedStatement = insertMapMappedStatement.getInsertMapMappedStatement();
		return sqlSessionManager.insert(mappedStatement.getId(), insertMapMappedStatement.createInsertMapParameter(tableName, valueMap));
	}
}
