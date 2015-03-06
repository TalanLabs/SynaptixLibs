package com.synaptix.swing;

import java.util.Date;

import com.synaptix.swing.simpletimeline.SimpleTimelineTaskRenderer;

public interface SimpleTask {

	public Date getDateMin();
	
	public Date getDateMax();
	
	public SimpleTimelineTaskRenderer getTaskRenderer();
	
	public boolean isNoClipping();
	
	public boolean isSelected();
	
	public boolean isShowIntersection();
	
}
