package com.synaptix.swing.roulement.event;

import java.util.EventListener;

public interface RoulementTimelineListener extends EventListener {

	public abstract void zoomChanged(int dayWidth);
	
	public abstract void horizontalScrollBarChanged(int value);
	
	public abstract void verticalScrollBarChanged(int value);
	
}
