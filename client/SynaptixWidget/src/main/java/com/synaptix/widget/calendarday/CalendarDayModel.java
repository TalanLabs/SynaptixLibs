package com.synaptix.widget.calendarday;

public interface CalendarDayModel {

	public int getColumnCount();

	public String getColumnName(int column);

	public int getRowCount(int column);

	public Object getValue(int column, int row);

	public void addCalendarDayModelListener(CalendarDayModelListener l);

	public void removeCalendarDayModelListener(CalendarDayModelListener l);

}
