package com.synaptix.widget.joda.text;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class LocalTimeFormat extends Format {

	private static final long serialVersionUID = 4443150226903263434L;

	private DateTimeFormatter dateTimeFormatter;

	public LocalTimeFormat(String pattern) {
		this(new DateTimeFormatterBuilder().appendPattern(pattern).toFormatter());
	}

	public LocalTimeFormat(DateTimeFormatter dateTimeFormatter) {
		super();

		this.dateTimeFormatter = dateTimeFormatter;
	}

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
		pos.setBeginIndex(0);
		pos.setEndIndex(0);
		toAppendTo.append(((LocalTime) obj).toString(dateTimeFormatter));
		return toAppendTo;
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		Object res = null;
		try {
			res = LocalTime.parse(source, dateTimeFormatter);
			pos.setIndex(source.length());
			pos.setErrorIndex(-1);
		} catch (Exception e) {
			pos.setIndex(0);
			pos.setErrorIndex(0);
		}
		return res;
	}
}
