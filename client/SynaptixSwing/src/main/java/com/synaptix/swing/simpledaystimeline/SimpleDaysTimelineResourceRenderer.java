package com.synaptix.swing.simpledaystimeline;

import java.awt.Component;

import com.synaptix.swing.JSimpleDaysTimeline;

public interface SimpleDaysTimelineResourceRenderer {

	public Component getSimpleDaysTimelineResourceRendererComponent(
			JSimpleDaysTimeline simpleDaysTimeline,
			SimpleDaysTimelineResource resource, int index, boolean isSelected,
			boolean hasFocus, boolean isDrag);

}
