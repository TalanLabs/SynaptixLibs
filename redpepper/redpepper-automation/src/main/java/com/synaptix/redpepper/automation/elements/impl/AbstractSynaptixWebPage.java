package com.synaptix.redpepper.automation.elements.impl;

import java.util.Map;

import com.synaptix.redpepper.automation.elements.facade.IWebElement;

/**
 * abstraction for Red pepper
 * 
 * @author skokaina
 * 
 */
public abstract class AbstractSynaptixWebPage extends AbstractWebPage {

	/**
	 * greenpepper Only, to manage via annotation processing => AbstractGreenPepperWebPage //<E extends Enum<E>>
	 */
	@Override
	public void initElement(IWebElement webElement) {
		super.initElement(webElement);
	}

	public abstract Map<String, String> getDebugIdMap();

}
