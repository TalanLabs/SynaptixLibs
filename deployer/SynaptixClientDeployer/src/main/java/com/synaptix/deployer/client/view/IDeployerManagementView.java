package com.synaptix.deployer.client.view;

import com.synaptix.client.view.IView;
import com.synaptix.deployer.client.deployerManagement.ManagementNode;
import com.synaptix.deployer.client.deployerManagement.ManagementNode.NodeType;
import com.synaptix.deployer.environment.IEnvironmentInstance;
import com.synaptix.deployer.environment.ISynaptixEnvironment;
import com.synaptix.deployer.instructions.SynaptixInstructions;
import com.synaptix.deployer.model.StepEnum;

public interface IDeployerManagementView extends IView {

	public void exploreNode(ManagementNode node);

	public void checkFinalStep(NodeType node);

	public void setRunning(boolean running);

	public void initResultPanel(ISynaptixEnvironment environment, SynaptixInstructions instructions);

	public void markDone(IEnvironmentInstance instance, StepEnum stepEnum);

	public void markRejected(IEnvironmentInstance instance, StepEnum step, String cause);

	public void markPlay(IEnvironmentInstance instance, StepEnum stepEnum);

	public void updateMailCheckbox();

	public boolean hasError();

	public void log(String log);

}
