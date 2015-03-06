package com.synaptix.mybatis.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes({ Boolean.class, boolean.class })
public class CharToBooleanTypeHandler extends BaseTypeHandler<Boolean> {

	private static final String YES = "1";

	private static final String NO = "0";

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
		boolean b = parameter.booleanValue();
		if (b) {
			ps.setString(i, YES);
		} else {
			ps.setString(i, NO);
		}
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String s = rs.getString(columnName);
		return parse(s);
	}

	@Override
	public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String s = cs.getString(columnIndex);
		return parse(s);
	}

	private Boolean parse(String s) {
		Boolean res = null;
		if (s != null) {
			if (s.equals(YES)) {
				res = Boolean.TRUE;
			} else if (s.equals(NO)) {
				res = Boolean.FALSE;
			} else {
				res = Boolean.FALSE;
			}
		}
		return res;
	}

	@Override
	public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String s = rs.getString(columnIndex);
		return parse(s);
	}
}
