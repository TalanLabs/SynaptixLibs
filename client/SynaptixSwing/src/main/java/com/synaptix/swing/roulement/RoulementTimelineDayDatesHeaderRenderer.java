package com.synaptix.swing.roulement;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.synaptix.swing.DayDate;

public interface RoulementTimelineDayDatesHeaderRenderer {

	public abstract void paintHeader(Graphics2D g,
			JRoulementTimelineDayDatesHeader header, DayDate dayDate,
			Rectangle rect);

	public abstract String getToolTipText(DayDate dayDate);

}
