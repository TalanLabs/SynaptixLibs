package com.synaptix.deployer.model;

import java.util.Set;

import com.synaptix.component.IComponent;
import com.synaptix.component.annotation.SynaptixComponent;
import com.synaptix.deployer.environment.IEnvironmentInstance;
import com.synaptix.deployer.environment.ISynaptixEnvironment;

@SynaptixComponent
public interface IEnvironmentState extends IComponent {

	public ISynaptixEnvironment getEnvironment();

	public void setEnvironment(ISynaptixEnvironment environment);

	public Set<IEnvironmentInstance> getRunningInstanceSet();

	public void setRunningInstanceSet(Set<IEnvironmentInstance> runningInstanceSet);

}