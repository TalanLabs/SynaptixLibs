package com.synaptix.swing.simpletimeline;

import java.awt.Graphics;
import java.awt.Rectangle;

import com.synaptix.swing.JSimpleTimeline;
import com.synaptix.swing.SimpleTask;

public interface SimpleTimelineTaskRenderer {

	public void paintTask(Graphics g, Rectangle rect,
			JSimpleTimeline simpleTimeline, SimpleTask task,
			boolean isSelected, boolean hasFocus, int ressource);

}
