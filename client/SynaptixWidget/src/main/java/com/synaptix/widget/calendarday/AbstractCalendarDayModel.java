package com.synaptix.widget.calendarday;

import javax.swing.event.EventListenerList;

public abstract class AbstractCalendarDayModel implements CalendarDayModel {

	protected EventListenerList listenerList = new EventListenerList();

	@Override
	public void addCalendarDayModelListener(CalendarDayModelListener l) {
		listenerList.add(CalendarDayModelListener.class, l);
	}

	@Override
	public void removeCalendarDayModelListener(CalendarDayModelListener l) {
		listenerList.remove(CalendarDayModelListener.class, l);
	}

	public CalendarDayModelListener[] getCalendarDayModelListeners() {
		return (CalendarDayModelListener[]) listenerList.getListeners(CalendarDayModelListener.class);
	}

	public void fireCalendarDayDataChanged() {
		fireCalendarDayChanged(new CalendarDayModelEvent(this));
	}

	public void fireCalendarDayChanged(CalendarDayModelEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == CalendarDayModelListener.class) {
				((CalendarDayModelListener) listeners[i + 1]).calendarDayChanged(e);
			}
		}
	}
}
