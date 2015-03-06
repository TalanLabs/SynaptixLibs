package com.synaptix.redpepper.automation.elements.impl;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import com.synaptix.redpepper.automation.drivers.SynchronizedDriver;
import com.synaptix.redpepper.automation.elements.facade.HasClickAction;
import com.synaptix.redpepper.automation.elements.facade.IWebElement;

/**
 * button element
 * 
 * @author skokaina
 * 
 */
public class WebButtonElement extends WebAutoElement implements HasClickAction {

	public WebButtonElement(IWebElement element, SynchronizedDriver driver) {
		super(element, driver);
	}

	public WebButtonElement(IWebElement element) {
		super(element);
	}

	@Override
	public void click() {
		WebElement find = frontEndDriver.find(wrappedElement);
		find.click();
	}

	@Override
	public void dbClick() {
		Actions action = new Actions(frontEndDriver.getWebDriver());
		WebElement find = frontEndDriver.find(wrappedElement);
		Action doubleClick = action.doubleClick(find).build();
		doubleClick.perform();
	}

}
