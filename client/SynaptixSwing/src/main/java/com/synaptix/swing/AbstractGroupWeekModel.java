package com.synaptix.swing;

import javax.swing.event.EventListenerList;

import com.synaptix.swing.event.GroupWeekModelEvent;
import com.synaptix.swing.event.GroupWeekModelListener;

public abstract class AbstractGroupWeekModel implements GroupWeekModel {

	protected EventListenerList listenerList;

	public AbstractGroupWeekModel() {
		listenerList = new EventListenerList();
	}

	public boolean isSelected(int group, int row, int day) {
		return true;
	}

	public void addGroupWeekModelListener(GroupWeekModelListener l) {
		listenerList.add(GroupWeekModelListener.class, l);
	}

	public void removeGroupWeekModelListener(GroupWeekModelListener l) {
		listenerList.remove(GroupWeekModelListener.class, l);
	}

	public GroupWeekModelListener[] getGroupWeekDataListeners() {
		return (GroupWeekModelListener[]) listenerList
				.getListeners(GroupWeekModelListener.class);
	}

	protected void fireDataChanged() {
		GroupWeekModelListener[] listeners = listenerList
				.getListeners(GroupWeekModelListener.class);
		GroupWeekModelEvent e = new GroupWeekModelEvent(this);

		for (GroupWeekModelListener listener : listeners) {
			listener.dataChanged(e);
		}
	}
}
