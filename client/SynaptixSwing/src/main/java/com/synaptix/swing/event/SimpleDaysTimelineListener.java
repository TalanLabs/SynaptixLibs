package com.synaptix.swing.event;

import java.util.EventListener;

public interface SimpleDaysTimelineListener extends EventListener {

	public abstract void zoomChanged(int dayWidth);
	
	public abstract void horizontalScrollBarChanged(int value);
	
	public abstract void verticalScrollBarChanged(int value);
	
}
