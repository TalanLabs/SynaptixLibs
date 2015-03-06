package com.synaptix.redpepper.automation.elements.impl;

import com.synaptix.redpepper.automation.drivers.SynchronizedDriver;
import com.synaptix.redpepper.automation.elements.facade.HasTextInput;
import com.synaptix.redpepper.automation.elements.facade.IWebElement;

/**
 * input element
 * 
 * @author skokaina
 * 
 */
public class WebInputElement extends WebAutoElement implements HasTextInput {

	public WebInputElement(IWebElement element, SynchronizedDriver driver) {
		super(element, driver);
	}

	public WebInputElement(IWebElement element) {
		super(element);
	}

	@Override
	public void setInput(String e) {
		frontEndDriver.find(wrappedElement).sendKeys(e);
	}

	@Override
	public String getValue() {
		return frontEndDriver.find(wrappedElement).getText();
	}
}
