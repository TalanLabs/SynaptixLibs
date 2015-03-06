package com.synaptix.swing;

import java.util.Date;
import java.util.List;

import com.synaptix.swing.event.TimelineModelListener;

public interface TimelineModel {

	public int getRessourceCount();
	
	public String getRessourceName(int ressource);
	
	public Date getDateMin();
	
	public Date getDateMax();
	
	public List<Task> getTasks(int ressource,Date dateMin,Date dateMax);
	
	public void addTimelineModelListener(TimelineModelListener l);
	
	public void removeTimelineModelListener(TimelineModelListener l);
}
