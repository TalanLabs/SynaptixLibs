package com.synaptix.widget.joda.view.swing.filter;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.synaptix.widget.util.StaticWidgetHelper;

public class LocalDateTimeSearch extends AbstractLocalSearch<LocalDateTime> {

	private static final long serialVersionUID = 4374698666605409477L;

	private DateTimeFormatter dateTimeFormatter;

	public LocalDateTimeSearch(int comparaisonType, LocalDateTime value) {
		super(comparaisonType, value);
	}

	@Override
	protected DateTimeFormatter getDateTimeFormatter() {
		if (dateTimeFormatter == null) {
			dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern(StaticWidgetHelper.getSynaptixDateConstantsBundle().displayDateTimeFormat()).toFormatter();
		}
		return dateTimeFormatter;
	}
}
