package com.synaptix.redpepper.automation.elements.impl;

import java.util.List;

import org.openqa.selenium.WebElement;

import com.synaptix.redpepper.automation.drivers.SynchronizedDriver;
import com.synaptix.redpepper.automation.elements.facade.IWebElement;

/**
 * auto-complete element
 * 
 * @author skokaina
 * 
 */
public class WebSuggestElement extends WebAutoElement {

	List<WebElement> suggestions;

	public WebSuggestElement(IWebElement element, SynchronizedDriver driver) {
		super(element, driver);
	}

	public WebSuggestElement(IWebElement element) {
		super(element);
	}

	public List<String> getSuggestions() {
		return null;
	}

	public void selectSuggestion(int index) {

	}

	public boolean hasSuggestionForKey(String key) {
		return false;
	}

}
