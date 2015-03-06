package com.synaptix.redpepper.automation.elements.facade;

import com.synaptix.redpepper.automation.drivers.SynchronizedDriver;

/**
 * 
 * @author skokaina
 * 
 */
public interface IDrivableWebPage {

	/**
	 * set the driver that will be used by the automation elements
	 */
	public void setDriver(SynchronizedDriver sDvr);

}
