package com.synaptix.widget.calendarday;

import java.awt.Component;

public interface CalendarDayCellRenderer {

	public Component getCalendarDayCellRendererComponent(JCalendarDay calendarDay, Object value, int column, int row, boolean selected);

}
