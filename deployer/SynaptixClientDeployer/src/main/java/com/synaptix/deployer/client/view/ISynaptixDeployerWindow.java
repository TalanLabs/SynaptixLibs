package com.synaptix.deployer.client.view;

import com.synaptix.client.view.IView;
import com.synaptix.deployer.client.core.SynaptixDeployerContext;

public interface ISynaptixDeployerWindow extends IView {

	public void setTitleSuffix(String title);

	public void start();

	public void builds();

	public void registerView(IView view);

	public void setSynaptixDeployerContext(SynaptixDeployerContext synaptixDeployerContext);

}
