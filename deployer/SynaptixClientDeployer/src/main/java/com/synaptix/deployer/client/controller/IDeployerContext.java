package com.synaptix.deployer.client.controller;

import com.synaptix.widget.core.controller.IController;

public interface IDeployerContext {

	/**
	 * Register a controller
	 * 
	 * @param controller
	 */
	public void registerController(IController controller);

}
