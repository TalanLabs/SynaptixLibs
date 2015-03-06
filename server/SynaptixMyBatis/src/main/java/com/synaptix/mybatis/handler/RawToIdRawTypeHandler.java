package com.synaptix.mybatis.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;
import oracle.sql.RAW;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeException;

import com.synaptix.entity.IdRaw;

@MappedTypes(IdRaw.class)
public class RawToIdRawTypeHandler extends BaseTypeHandler<IdRaw> {

	@Override
	public void setParameter(PreparedStatement ps, int i, IdRaw parameter, JdbcType jdbcType) throws SQLException {
		if (parameter == null) {
			try {
				ps.setNull(i, OracleTypes.RAW);
			} catch (SQLException e) {
				throw new TypeException("Error setting null for parameter #" + i + " with JdbcType " + jdbcType + " . "
						+ "Try setting a different JdbcType for this parameter or a different jdbcTypeForNull configuration property. " + "Cause: " + e, e);
			}
		} else {
			setNonNullParameter(ps, i, parameter, jdbcType);
		}
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, IdRaw parameter, JdbcType jdbcType) throws SQLException {
		ps.setObject(i, new MyRAW(RAW.hexString2Bytes(parameter.getHex())));
	}

	@Override
	public IdRaw getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String bs = rs.getString(columnName);
		return parse(bs);
	}

	@Override
	public IdRaw getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String bs = cs.getString(columnIndex);
		return parse(bs);
	}

	private IdRaw parse(String bs) throws SQLException {
		return bs != null ? new IdRaw(bs) : null;
	}

	@Override
	public IdRaw getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String bs = rs.getString(columnIndex);
		return parse(bs);
	}
}
