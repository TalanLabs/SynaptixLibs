package com.synaptix.pmgr.model;

import com.synaptix.component.IComponent;
import com.synaptix.pmgr.core.lib.ProcessingChannel.Agent;

public interface IHeartbeatProcessDefinition extends IComponent {

	public Integer getSeconds();

	public void setSeconds(Integer seconds);

	public String getBeat();

	public void setBeat(String beat);

	public Class<? extends Agent> getAgentClass();

	public void setAgentClass(Class<? extends Agent> agentClass);

}
