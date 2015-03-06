package com.synaptix.widget.core.controller;

import com.synaptix.client.view.IView;

public interface IController {

	/**
	 * Unique identifier
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * Human name controller
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * View
	 * 
	 * @return
	 */
	public IView getView();

	public void start();

	/**
	 * The controller might return false if it doesn't want to end
	 * TODO: modify FrontendController to support that functionnality
	 */
	public boolean stop();

}
