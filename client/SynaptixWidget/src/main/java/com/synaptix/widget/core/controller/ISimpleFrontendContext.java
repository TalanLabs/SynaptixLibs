package com.synaptix.widget.core.controller;

import com.synaptix.client.view.IView;

public interface ISimpleFrontendContext {

	/**
	 * Register a controller
	 * 
	 * @param controller
	 */
	public void registerController(IController controller);

	/**
	 * Unregister a controller
	 * 
	 * @param controller
	 */
	public void unregisterController(IController controller);

	/**
	 * Opens the help dialog
	 * 
	 * @param parent
	 * @param key
	 */
	public void showHelp(IView parent, String key);

}
