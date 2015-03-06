package com.synaptix.swing.event;

import java.util.EventListener;

public interface TimelineModelListener extends EventListener {

	public void timelineChanged(TimelineModelEvent e);
	
}
