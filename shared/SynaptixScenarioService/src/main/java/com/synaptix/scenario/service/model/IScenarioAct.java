package com.synaptix.scenario.service.model;

import java.util.Comparator;

import com.synaptix.component.IComponent;

public interface IScenarioAct extends IComponent {
	
	public static final Comparator<IScenarioAct> DEFAULT_SCENARIO_ACT_COMPARATOR = new Comparator<IScenarioAct>() {
		
		public int compare(final IScenarioAct scenarioActOne, final IScenarioAct scenarioActTwo) {
			return (int)(scenarioActOne.getStartTime() - scenarioActTwo.getStartTime());
		}
	};

	/**
	 * get start time
	 * 
	 */
	public abstract long getStartTime();
	
	
	/**
	 * set start time
	 * 
	 * @param startTime
	 */
	public abstract void setStartTime(final long startTime);
	
	
	/**
	 * get xml 
	 * 
	 */
	public abstract String getXMLExtension();
	
	
	/**
	 * set xml 
	 * 
	 * @param xmlExtension
	 */
	public abstract void setXMLExtension(final String xmlExtension);
	
	
	/**
	 * get child 
	 * 
	 */
	public abstract IScenarioAct getChild();
	
	
	/**
	 * set child 
	 * 
	 */
	public abstract void setChild(final IScenarioAct child);
	
	/**
	 * get name 
	 * 
	 */
	public abstract String getName();
	
	
	/**
	 * set name 
	 * 
	 */
	public abstract void setName(final String name);
}
