package com.synaptix.mybatis.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.joda.time.Duration;

@MappedTypes(Duration.class)
public class NumberToDurationTypeHandler extends BaseTypeHandler<Duration> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Duration parameter, JdbcType jdbcType) throws SQLException {
		ps.setLong(i, parameter != null ? parameter.getMillis() / 1000L : null);
	}

	@Override
	public Duration getNullableResult(ResultSet rs, String columnName) throws SQLException {
		long seconds = rs.getLong(columnName);
		return new Duration(seconds * 1000L);
	}

	@Override
	public Duration getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		long seconds = cs.getLong(columnIndex);
		return new Duration(seconds * 1000L);
	}

	@Override
	public Duration getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		long seconds = rs.getLong(columnIndex);
		return new Duration(seconds * 1000L);
	}
}
