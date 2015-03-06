package com.synaptix.mybatis.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.joda.time.LocalDateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

@MappedTypes(LocalDateTime.class)
public class DateToLocalDateTimeTypeHandler extends BaseTypeHandler<LocalDateTime> {

	private static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern("dd/MM/yyyyHH:mm:ss").toFormatter().withChronology(ISOChronology.getInstanceUTC());

	private static final String pattern = "dd/MM/yyyyHH:mm:ss";
	private static final TimeZone tzUTC = TimeZone.getTimeZone("UTC");

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType) throws SQLException {
		ps.setTimestamp(i, parameter != null ? new Timestamp(parameter.toDate().getTime()) : null);
	}

	@Override
	public LocalDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
		Timestamp t = rs.getTimestamp(columnName);
		return parse(t);
	}

	@Override
	public LocalDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		Timestamp t = cs.getTimestamp(columnIndex);
		return parse(t);
	}

	private static final String format(Timestamp timestamp) {
		return DateFormatUtils.format(timestamp, pattern, tzUTC);
	}

	private LocalDateTime parse(Timestamp t) {
		if (t != null) {
			try {
				return LocalDateTime.parse(format(t), dateTimeFormatter);
			} catch (RuntimeException e) {
				System.out.println(t);
				System.out.println(format(t));
				throw e;
			}
		}
		return null;
	}

	@Override
	public LocalDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		Timestamp t = rs.getTimestamp(columnIndex);
		return parse(t);
	}
}
