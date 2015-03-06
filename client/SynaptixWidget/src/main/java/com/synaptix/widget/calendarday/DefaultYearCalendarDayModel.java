package com.synaptix.widget.calendarday;

import org.joda.time.LocalDate;

import com.synaptix.widget.util.StaticWidgetHelper;

public class DefaultYearCalendarDayModel extends AbstractCalendarDayModel {

	private int year;

	private int firstMonth;

	private int nbMonth;

	public DefaultYearCalendarDayModel() {
		this(LocalDate.now().getYear());
	}

	public DefaultYearCalendarDayModel(int year) {
		this(year, 1, 12);
	}

	public DefaultYearCalendarDayModel(int year, int firstMonth, int nbMonth) {
		super();

		this.year = year;
		this.firstMonth = firstMonth;
		this.nbMonth = nbMonth;
	}

	public int getYear() {
		return year;
	}

	public int getFirstMonth() {
		return firstMonth;
	}

	public int getNbMonth() {
		return nbMonth;
	}

	public void setYear(int year) {
		this.year = year;
		fireCalendarDayDataChanged();
	}

	public void setMonth(int firstMonth, int nbMonth) {
		this.firstMonth = firstMonth;
		this.nbMonth = nbMonth;
		fireCalendarDayDataChanged();
	}

	@Override
	public int getColumnCount() {
		return nbMonth;
	}

	@Override
	public String getColumnName(int column) {
		return StaticWidgetHelper.getSynaptixDateConstantsBundle().monthsList()[(firstMonth + column - 1) % 12];
	}

	@Override
	public int getRowCount(int column) {
		int nbYears = (firstMonth + column - 1) / 12;
		LocalDate date = new LocalDate(year + nbYears, (firstMonth + column - 1) % 12 + 1, 1);
		return date.dayOfMonth().getMaximumValue();
	}

	@Override
	public Object getValue(int column, int row) {
		int nbYears = (firstMonth + column - 1) / 12;
		LocalDate date = new LocalDate(year + nbYears, (firstMonth + column - 1) % 12 + 1, row + 1);
		return date;
	}
}
