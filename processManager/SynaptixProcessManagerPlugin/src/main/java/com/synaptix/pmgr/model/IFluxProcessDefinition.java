package com.synaptix.pmgr.model;

import com.synaptix.pmgr.agent.IAgent;
import com.synaptix.pmgr.trigger.message.IFlux;

public interface IFluxProcessDefinition<M extends IFlux> extends IProcessDefinition {

	public Class<M> getFluxClass();

	public void setFluxClass(Class<M> fluxClass);

	public Class<? extends IAgent<M>> getAgentClass();

	public void setAgentClass(Class<? extends IAgent<M>> agentClass);

}
