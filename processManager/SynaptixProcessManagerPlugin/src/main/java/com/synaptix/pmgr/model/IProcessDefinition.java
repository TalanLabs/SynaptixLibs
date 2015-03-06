package com.synaptix.pmgr.model;

import com.synaptix.component.IComponent;

public interface IProcessDefinition extends IComponent {

	@EqualsKey
	public String getName();

	public void setName(String name);

	public int getMaxWorking();

	public void setMaxWorking(int maxWorking);

	public int getMaxWaiting();

	public void setMaxWaiting(int maxWaiting);

	public ProcessType getProcessType();

	public void setProcessType(ProcessType processType);

}
