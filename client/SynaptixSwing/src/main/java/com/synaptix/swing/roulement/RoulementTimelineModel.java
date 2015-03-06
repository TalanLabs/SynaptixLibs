package com.synaptix.swing.roulement;

import java.util.List;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.roulement.event.RoulementTimelineModelListener;

public interface RoulementTimelineModel {

	public int getResourceCount();

	public String getResourceName(int resource);

	public List<RoulementTask> getTasks(int resource, DayDate dayDateMin,
			DayDate dayDateMax);

	public void addRoulementTimelineModelListener(
			RoulementTimelineModelListener l);

	public void removeRoulementTimelineModelListener(
			RoulementTimelineModelListener l);

}
