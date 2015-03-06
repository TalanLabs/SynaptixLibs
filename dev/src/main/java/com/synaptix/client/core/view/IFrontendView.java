package com.synaptix.client.core.view;

import com.synaptix.client.view.IView;

public interface IFrontendView extends IView {

	/**
	 * Start view
	 */
	public void start();

	public void loads();

	public void saves();

	/**
	 * Stop view
	 */
	public void stop();

	/**
	 * Register view
	 * 
	 * @param view
	 */
	public void registerView(IView view);

	/**
	 * 
	 */
	public void builds();

	/**
	 * Set URL for map
	 * 
	 * @param backgroundWmsUrl
	 * @param cityWmsUrl
	 * @param countryWmsUrl
	 */
	public void setMapUrls(String backgroundWmsUrl, String cityWmsUrl, String countryWmsUrl);

	/**
	 * Sets the database ip to display database name
	 * 
	 * @param databaseIp
	 */
	public void setTitleSuffix(String databaseIp);

	/**
	 * Sets the connection state text
	 * 
	 * @param disconnected
	 */
	public void setConnectionState(String connectionState);

}
