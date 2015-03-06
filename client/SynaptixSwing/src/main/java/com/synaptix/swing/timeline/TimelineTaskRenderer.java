package com.synaptix.swing.timeline;

import java.awt.Component;

import com.synaptix.swing.JTimeline;
import com.synaptix.swing.Task;

public interface TimelineTaskRenderer {

	public boolean isUsePreferredSize();
	
	Component getTimelineTaskRendererComponent(JTimeline timeline,Task task,
			boolean isSelected, boolean hasFocus, int ressource,int width,int height);

}
