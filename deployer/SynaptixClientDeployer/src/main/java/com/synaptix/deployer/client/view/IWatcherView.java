package com.synaptix.deployer.client.view;

import com.synaptix.client.view.IView;

public interface IWatcherView extends IView {

	/**
	 * Add a line
	 * 
	 * @param line
	 */
	public void addConsoleLine(String line);

	/**
	 * Switch buttons depending on the running state
	 * 
	 * @param running
	 */
	public void setRunning(boolean running);

	/**
	 * Change title and subtitle
	 * 
	 * @param title
	 * @param subtitle
	 */
	public void setTitle(String title, String subtitle);

	/**
	 * Reveal
	 */
	public void reveal();

}
