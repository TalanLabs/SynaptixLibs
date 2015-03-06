package com.synaptix.swing.simpledaystimeline;

import java.awt.Point;
import java.awt.Rectangle;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.SimpleDaysTask;

public abstract class AbstractSimpleDaysTimelineTaskRenderer implements
		SimpleDaysTimelineTaskRenderer {

	public double getSelectionHeightPourcent() {
		return 1.0;
	}

	public String getToolTipText(Rectangle rect,
			JSimpleDaysTimeline simpleDaysTimeline, SimpleDaysTask task,
			int resource, DayDate dayDate, Point point) {
		return null;
	}
}
