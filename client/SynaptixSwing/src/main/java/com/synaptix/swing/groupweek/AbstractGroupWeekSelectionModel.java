package com.synaptix.swing.groupweek;

import javax.swing.event.EventListenerList;

public abstract class AbstractGroupWeekSelectionModel implements
		GroupWeekSelectionModel {

	protected EventListenerList listenerList;

	public AbstractGroupWeekSelectionModel() {
		listenerList = new EventListenerList();
	}

	public void addGroupWeekSelectionListener(GroupWeekSelectionListener l) {
		listenerList.add(GroupWeekSelectionListener.class, l);
	}

	public void removeGroupWeekSelectionListener(GroupWeekSelectionListener l) {
		listenerList.remove(GroupWeekSelectionListener.class, l);
	}

	public GroupWeekSelectionListener[] getGroupWeekSelectionListeners() {
		return (GroupWeekSelectionListener[]) listenerList
				.getListeners(GroupWeekSelectionListener.class);
	}

	protected void fireGroupWeekChanged() {
		GroupWeekSelectionListener[] listeners = listenerList
				.getListeners(GroupWeekSelectionListener.class);

		for (GroupWeekSelectionListener listener : listeners) {
			listener.selectionChanged();
		}
	}
}
