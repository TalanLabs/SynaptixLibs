package com.synaptix.swing.event;

import java.util.EventListener;

public interface SimpleTimelineListener extends EventListener {

	public void datesChanged();

	public void zoomChanged();
	
}
