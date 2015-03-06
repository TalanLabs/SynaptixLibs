package com.synaptix.mybatis.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.joda.time.LocalTime;

@MappedTypes(LocalTime.class)
public class DateToLocalTimeTypeHandler extends BaseTypeHandler<LocalTime> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, LocalTime parameter, JdbcType jdbcType) throws SQLException {
		Calendar c = Calendar.getInstance();
		c.set(1970, Calendar.JANUARY, 1, parameter.getHourOfDay(), parameter.getMinuteOfHour(), parameter.getSecondOfMinute());
		ps.setTime(i, new Time(c.getTimeInMillis()));
	}

	@Override
	public LocalTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
		Time t = rs.getTime(columnName);
		return parse(t);
	}

	@Override
	public LocalTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		Time t = cs.getTime(columnIndex);
		return parse(t);
	}

	private LocalTime parse(Time t) {
		if (t != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(t);
			return new LocalTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
		}
		return null;
	}

	@Override
	public LocalTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		Time t = rs.getTime(columnIndex);
		return parse(t);
	}
}
