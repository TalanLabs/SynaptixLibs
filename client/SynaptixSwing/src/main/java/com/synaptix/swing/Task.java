package com.synaptix.swing;

import java.util.Date;

import com.synaptix.swing.timeline.TimelineTaskRenderer;

public interface Task {

	public Date getDateMin();
	
	public Date getDateMax();
	
	public String getTitle();
	
	public TimelineTaskRenderer getTaskRenderer();
	
	public Task getLiaison();
	
	public int getPriority();
	
	public boolean isSelected();
}
