package com.synaptix.mybatis.handler;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.joda.time.LocalDate;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

@MappedTypes(LocalDate.class)
public class DateToLocalDateTypeHandler extends BaseTypeHandler<LocalDate> {

	private static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern("dd/MM/yyyy").toFormatter().withChronology(ISOChronology.getInstanceUTC());

	private static final String pattern = "dd/MM/yyyy";
	private static final TimeZone tzUTC = TimeZone.getTimeZone("UTC");

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, LocalDate parameter, JdbcType jdbcType) throws SQLException {
		ps.setDate(i, parameter != null ? new Date(parameter.toDate().getTime()) : null);
	}

	@Override
	public LocalDate getNullableResult(ResultSet rs, String columnName) throws SQLException {
		Date t = rs.getDate(columnName);
		return parse(t);
	}

	private static final String format(Date date) {
		return DateFormatUtils.format(date, pattern, tzUTC);
	}

	@Override
	public LocalDate getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		Date t = cs.getDate(columnIndex);
		return parse(t);
	}

	private LocalDate parse(Date t) {
		if (t != null) {
			return LocalDate.parse(format(t), dateTimeFormatter);
		}
		return null;
	}

	@Override
	public LocalDate getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		Date t = rs.getDate(columnIndex);
		return parse(t);
	}
}
