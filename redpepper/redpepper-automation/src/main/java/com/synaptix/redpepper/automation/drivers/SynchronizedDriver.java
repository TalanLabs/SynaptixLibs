package com.synaptix.redpepper.automation.drivers;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.synaptix.redpepper.automation.elements.facade.IWebElement;

/**
 * 
 * @author skokaina
 * 
 */
// FIXME add abstraction to avoid talking directly to selenium
public interface SynchronizedDriver {

	/**
	 * 
	 * @param element
	 *            locator description
	 * @return
	 */
	public WebElement find(IWebElement element);

	/**
	 * selenium driver
	 * 
	 * @return
	 */
	public WebDriver getWebDriver();

}
