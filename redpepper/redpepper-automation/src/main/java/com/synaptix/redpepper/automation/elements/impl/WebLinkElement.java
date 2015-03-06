package com.synaptix.redpepper.automation.elements.impl;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.synaptix.redpepper.automation.drivers.SynchronizedDriver;
import com.synaptix.redpepper.automation.elements.facade.HasClickAction;
import com.synaptix.redpepper.automation.elements.facade.IWebElement;
import com.synaptix.redpepper.automation.utils.ISyncCall;

/**
 * link element
 * 
 * @author skokaina
 * 
 */
public class WebLinkElement extends WebAutoElement implements HasClickAction {

	public WebLinkElement(IWebElement element, SynchronizedDriver driver) {
		super(element, driver);
	}

	public WebLinkElement(IWebElement element) {
		super(element);
	}

	@Override
	public void click() {
		safeAction(new ISyncCall() {
			@Override
			public void execute(WebElement e) {
				e.click();
			}
		});
	}

	@Override
	public void dbClick() {
		safeAction(new ISyncCall() {
			@Override
			public void execute(WebElement e) {
				Actions action = new Actions(frontEndDriver.getWebDriver());
				action.doubleClick(e);
				action.perform();
				e.click();
			}
		});
	}

}
