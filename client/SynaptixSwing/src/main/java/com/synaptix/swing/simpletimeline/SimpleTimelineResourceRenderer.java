package com.synaptix.swing.simpletimeline;

import java.awt.Component;

import com.synaptix.swing.JSimpleTimeline;

public interface SimpleTimelineResourceRenderer {

	public Component getSimpleTimelineResourceRendererComponent(
			JSimpleTimeline simpleTimeline, SimpleTimelineResource resource,
			int index, boolean isSelected, boolean hasFocus, boolean isDrag);

}
