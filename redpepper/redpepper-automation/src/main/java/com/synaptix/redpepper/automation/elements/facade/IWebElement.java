package com.synaptix.redpepper.automation.elements.facade;

import com.synaptix.redpepper.web.annotations.gwt.shared.AutoWebType;

/**
 * 
 * @author skokaina
 * 
 */
public interface IWebElement {

	/**
	 * locator methods to use in repo
	 * 
	 */
	public static enum LocationMethod {
		CSS,
		ID,
		XPATH
	}

	public void setName(String name);

	public String getName();

	public String getLocator();

	public void setLocator(String locator);

	public LocationMethod getMethod();

	public void setMethod(LocationMethod method);

	public int getPosition();

	public void setPosition(int position);

	public AutoWebType getType();

	public void setType(AutoWebType type);
}
