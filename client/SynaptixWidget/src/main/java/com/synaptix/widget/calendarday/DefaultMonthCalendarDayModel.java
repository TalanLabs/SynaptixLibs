package com.synaptix.widget.calendarday;

import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import com.synaptix.widget.util.StaticWidgetHelper;

public class DefaultMonthCalendarDayModel extends AbstractCalendarDayModel {

	private int year;

	private int month;

	private LocalDate startDate;

	private int nbWeek;

	public DefaultMonthCalendarDayModel() {
		this(LocalDate.now().getYear(), LocalDate.now().getMonthOfYear());
	}

	public DefaultMonthCalendarDayModel(int year, int month) {
		super();

		this.year = year;
		this.month = month;

		init();
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
		init();
		fireCalendarDayDataChanged();
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
		init();
		fireCalendarDayDataChanged();
	}

	private void init() {
		LocalDate firstDate = new LocalDate(year, month, 1);
		startDate = firstDate.withDayOfWeek(DateTimeConstants.MONDAY);
		LocalDate lastDate = firstDate.withDayOfMonth(firstDate.dayOfMonth().getMaximumValue());
		LocalDate endDate;
		if (lastDate.getDayOfWeek() != DateTimeConstants.SUNDAY) {
			endDate = lastDate.withDayOfWeek(DateTimeConstants.SUNDAY);
		} else {
			endDate = lastDate;
		}
		Duration d = new Duration(startDate.toDateMidnight(DateTimeZone.UTC), endDate.toDateMidnight(DateTimeZone.UTC));
		nbWeek = ((int) d.getStandardDays() + 1) / 7;
	}

	@Override
	public int getColumnCount() {
		return 7;
	}

	@Override
	public String getColumnName(int column) {
		return StaticWidgetHelper.getSynaptixDateConstantsBundle().daysList()[column];
	}

	@Override
	public int getRowCount(int column) {
		return nbWeek;
	}

	@Override
	public Object getValue(int column, int row) {
		return startDate.plusDays(row * 7 + column);
	}
}
