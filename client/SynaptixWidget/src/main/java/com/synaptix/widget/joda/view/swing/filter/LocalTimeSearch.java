package com.synaptix.widget.joda.view.swing.filter;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.synaptix.widget.util.StaticWidgetHelper;

public class LocalTimeSearch extends AbstractLocalSearch<LocalTime> {

	private static final long serialVersionUID = 4374698666605409477L;

	private DateTimeFormatter dateTimeFormatter;

	public LocalTimeSearch(int comparaisonType, LocalTime value) {
		super(comparaisonType, value);
	}

	@Override
	protected DateTimeFormatter getDateTimeFormatter() {
		if (dateTimeFormatter == null) {
			dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern(StaticWidgetHelper.getSynaptixDateConstantsBundle().displayTimeFormat()).toFormatter();
		}
		return dateTimeFormatter;
	}
}
