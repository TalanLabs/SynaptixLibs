package com.synaptix.swing.simpledaystimeline;

import java.util.List;

import com.synaptix.swing.SimpleDaysTask;

public interface SimpleDaysTimelineGroupTask extends IDayDatesMinMax {

	public abstract List<SimpleDaysTask> getSimpleDaysTaskList();

}
