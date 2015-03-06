package com.synaptix.mybatis.dao.mapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.jdbc.SQL;

public class SequenceProvider {

	private static final Log LOG = LogFactory.getLog(SequenceProvider.class);

	public static String selectSequence(String sqlObjectName) {
		SQL sqlBuilder = new SQL();
		sqlBuilder.SELECT(sqlObjectName + ".nextval");
		sqlBuilder.FROM("dual");
		String sql = sqlBuilder.toString();
		if (LOG.isDebugEnabled()) {
			LOG.debug(sql);
		}
		return sql;
	}

}
