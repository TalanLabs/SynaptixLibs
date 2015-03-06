package com.synaptix.swing;

import java.util.Date;
import java.util.List;

import com.synaptix.swing.event.SimpleTimelineModelListener;

public interface SimpleTimelineModel {

	public int getResourceCount();
	
	public String getResourceName(int resource);
	
	public List<SimpleTask> getTasks(int resource,Date dateMin,Date dateMax);
	
	public void addSimpleTimelineModelListener(SimpleTimelineModelListener l);
	
	public void removeSimpleTimelineModelListener(SimpleTimelineModelListener l);
	
}
