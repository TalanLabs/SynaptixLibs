package com.synaptix.swing.timeline;

import java.awt.Component;

import com.synaptix.swing.JTimeline;

public interface TimelineRessourceRenderer {

	public Component getTimelineRessourceRendererComponent(JTimeline timeline,
			TimelineRessource ressource, int index,boolean isSelected,boolean hasFocus);

}
