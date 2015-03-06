package com.synaptix.scenario.service.model;

import java.util.List;

import com.synaptix.component.IComponent;

public interface IScenario extends IComponent {

	/**
	 * get IScenarioAct List 
	 * 
	 */
	public List<IScenarioAct> getScenarioActList();
	
	/**
	 * set IScenarioAct List 
	 * 
	 * @param scenarioActList
	 */
	public void setScenarioActList(final List<IScenarioAct> scenarioActList);
	
}
