package com.synaptix.swing.roulement;

import java.awt.Point;
import java.awt.Rectangle;

import com.synaptix.swing.DayDate;

public abstract class AbstractRoulementTimelineTaskRenderer implements
		RoulementTimelineTaskRenderer {

	public double getSelectionHeightPourcent() {
		return 1.0;
	}

	public String getToolTipText(Rectangle rect,
			JRoulementTimeline roulementTimeline, RoulementTask task,
			int resource, DayDate dayDate, Point point) {
		return null;
	}
}
