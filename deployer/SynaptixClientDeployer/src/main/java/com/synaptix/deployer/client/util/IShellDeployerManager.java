package com.synaptix.deployer.client.util;

import com.synaptix.deployer.SynaptixDeployer;
import com.synaptix.deployer.environment.ISynaptixEnvironment;

public interface IShellDeployerManager {

	public SynaptixDeployer getShellDeployer(ISynaptixEnvironment environment);

}
