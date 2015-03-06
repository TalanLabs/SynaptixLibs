package com.synaptix.pmgr.model;

import com.synaptix.pmgr.core.lib.ProcessingChannel.Agent;

public interface ISimpleProcessDefinition extends IProcessDefinition {

	public Class<? extends Agent> getAgentClass();

	public void setAgentClass(Class<? extends Agent> agentClass);

}
