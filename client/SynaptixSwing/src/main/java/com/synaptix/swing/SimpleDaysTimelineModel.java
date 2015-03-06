package com.synaptix.swing;

import java.util.List;

import com.synaptix.swing.event.SimpleDaysTimelineModelListener;

public interface SimpleDaysTimelineModel {

	public int getResourceCount();

	public String getResourceName(int resource);

	public List<SimpleDaysTask> getTasks(int resource, DayDate dayDateMin,
			DayDate dayDateMax);

	public void addSimpleDaysTimelineModelListener(
			SimpleDaysTimelineModelListener l);

	public void removeSimpleDaysTimelineModelListener(
			SimpleDaysTimelineModelListener l);

}
