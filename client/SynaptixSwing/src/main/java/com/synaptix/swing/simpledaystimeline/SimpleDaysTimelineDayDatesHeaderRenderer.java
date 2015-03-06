package com.synaptix.swing.simpledaystimeline;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.synaptix.swing.DayDate;

public interface SimpleDaysTimelineDayDatesHeaderRenderer {

	public abstract void paintHeader(Graphics2D g,
			JSimpleDaysTimelineDayDatesHeader header, DayDate dayDate,
			Rectangle rect);

	public abstract String getToolTipText(DayDate dayDate);

}
