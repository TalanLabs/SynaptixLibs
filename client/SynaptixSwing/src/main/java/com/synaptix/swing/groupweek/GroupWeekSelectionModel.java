package com.synaptix.swing.groupweek;

public interface GroupWeekSelectionModel {

	public abstract int getSelectedGroup();

	public abstract int getSelectedRow();

	public abstract int getSelectedDay();

	public abstract void setSelected(int group, int row, int day);

	public abstract void clearSelection();

	public abstract boolean isSelection();

	public abstract void addGroupWeekSelectionListener(
			GroupWeekSelectionListener l);

	public abstract void removeGroupWeekSelectionListener(
			GroupWeekSelectionListener l);

}
