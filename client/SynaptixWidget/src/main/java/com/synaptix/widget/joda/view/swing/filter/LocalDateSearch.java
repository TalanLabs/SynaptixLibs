package com.synaptix.widget.joda.view.swing.filter;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.synaptix.widget.util.StaticWidgetHelper;

public class LocalDateSearch extends AbstractLocalSearch<LocalDate> {

	private static final long serialVersionUID = 4374698666605409477L;

	private DateTimeFormatter dateTimeFormatter;

	public LocalDateSearch(int comparaisonType, LocalDate value) {
		super(comparaisonType, value);
	}

	@Override
	protected DateTimeFormatter getDateTimeFormatter() {
		if (dateTimeFormatter == null) {
			dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern(StaticWidgetHelper.getSynaptixDateConstantsBundle().displayDateFormat()).toFormatter();
		}
		return dateTimeFormatter;
	}
}
