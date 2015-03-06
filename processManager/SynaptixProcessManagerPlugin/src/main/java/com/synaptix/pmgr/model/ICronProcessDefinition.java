package com.synaptix.pmgr.model;

import com.synaptix.component.IComponent;
import com.synaptix.pmgr.core.lib.ProcessingChannel.Agent;

public interface ICronProcessDefinition extends IComponent {

	public String getSchedulingPattern();

	public void setSchedulingPattern(String schedulingPattern);

	public Class<? extends Agent> getAgentClass();

	public void setAgentClass(Class<? extends Agent> agentClass);

}
