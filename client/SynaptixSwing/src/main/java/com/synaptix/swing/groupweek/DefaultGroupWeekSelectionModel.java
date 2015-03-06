package com.synaptix.swing.groupweek;

import javax.swing.event.EventListenerList;

public class DefaultGroupWeekSelectionModel extends
		AbstractGroupWeekSelectionModel {

	private int group = -1;

	private int row = -1;

	private int day = -1;

	public DefaultGroupWeekSelectionModel() {
		listenerList = new EventListenerList();
	}

	public int getSelectedGroup() {
		return group;
	}

	public int getSelectedRow() {
		return row;
	}

	public int getSelectedDay() {
		return day;
	}

	public void setSelected(int group, int row, int day) {
		this.group = group;
		this.row = row;
		this.day = day;
		fireGroupWeekChanged();
	}

	public void clearSelection() {
		setSelected(-1, -1, -1);
	}

	public boolean isSelection() {
		return group != -1 && row != -1 && day != -1;
	}
}
