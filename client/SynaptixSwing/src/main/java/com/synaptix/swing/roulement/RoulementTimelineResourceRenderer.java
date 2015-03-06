package com.synaptix.swing.roulement;

import java.awt.Component;

public interface RoulementTimelineResourceRenderer {

	public Component getRoulementTimelineLeftResourceRendererComponent(
			JRoulementTimeline roulementTimeline,
			RoulementTimelineResource resource, int index, boolean isSelected,
			boolean hasFocus, boolean isDrag);

	public Component getRoulementTimelineRightResourceRendererComponent(
			JRoulementTimeline roulementTimeline,
			RoulementTimelineResource resource, int index, boolean isSelected,
			boolean hasFocus, boolean isDrag);

}
