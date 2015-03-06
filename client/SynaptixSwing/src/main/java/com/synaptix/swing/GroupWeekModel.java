package com.synaptix.swing;

import com.synaptix.swing.event.GroupWeekModelListener;

public interface GroupWeekModel {

	public abstract int getGroupCount();

	public abstract int getGroupRowCount(int group);

	public abstract Object getValue(int group, int row, int day);
	
	public abstract boolean isSelected(int group, int row, int day);

	public abstract void addGroupWeekModelListener(GroupWeekModelListener l);

	public abstract void removeGroupWeekModelListener(GroupWeekModelListener l);

}
