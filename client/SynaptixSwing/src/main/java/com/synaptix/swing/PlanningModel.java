package com.synaptix.swing;

import java.util.Date;
import java.util.List;

import com.synaptix.swing.event.PlanningDataListener;

public interface PlanningModel {

	public List<Activity> getActivities(Date dateMin,Date dateMax);
	
	public void addPlanningDataListener(PlanningDataListener l);
	
	public void removePlanningDataListener(PlanningDataListener l);
	
}
