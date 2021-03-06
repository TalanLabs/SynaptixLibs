package com.synaptix.redpepper.automation.elements.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.synaptix.redpepper.automation.drivers.SynchronizedDriver;
import com.synaptix.redpepper.automation.elements.facade.IFeedableWebPage;
import com.synaptix.redpepper.automation.elements.facade.IWebElement;
import com.synaptix.redpepper.automation.elements.facade.IWebElement.LocationMethod;
import com.synaptix.redpepper.web.annotations.gwt.shared.AutoWebType;

/**
 * 
 * Page fixture abstraction, initializes fixture elements's locators based on wiki definitions
 * 
 * @author skokaina
 * 
 */
public abstract class AbstractWebPage implements IFeedableWebPage {

	public String beanClassName; // the bean class name
	Map<String, IWebElement> elements = new HashMap<String, IWebElement>();
	protected Map<String, WebAutoElement> autoElements = new HashMap<String, WebAutoElement>();
	private String pageName;

	/**
	 * 
	 * @param elementDefinition
	 */
	@Override
	public void initElement(IWebElement e) {
		initElement(e.getName(), e.getType().name(), e.getMethod().name(), e.getLocator(), e.getPosition());
	}

	/**
	 * Done for each page element based on a feeder
	 * 
	 * @param name
	 * @param method
	 * @param locator
	 * @param position
	 */
	protected void initElement(String name, String type, String method, String locator, Integer position) {
		/**
		 * used to locate an element
		 */
		DefaultWebElement defaultWebElement = new DefaultWebElement(name, AutoWebType.valueOf(type), locator, LocationMethod.valueOf(method), position);
		elements.put(name, defaultWebElement);

		/**
		 * selenium wrapper field initalizarion when it comes to greenpepper
		 */
		try {
			IWebElement iWebElement = elements.get(name);
			if (iWebElement != null) {
				WebAutoElement execAutoClass = ElementFactory.getElement(iWebElement);
				// for this abstract page, init fields (for java classes only
				for (Field f : this.getClass().getFields()) {
					Class<?> automationClass = f.getType();
					if (WebAutoElement.class.isAssignableFrom(automationClass)) {
						if (f.getName().equals(name)) {
							try {
								BeanUtils.setProperty(this, name, execAutoClass);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				autoElements.put(name, execAutoClass);
			} else {
				// throw something
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Convenient method to call an element based on the page enclosed fields' enum
	 */
	public IWebElement getElement(String token) {
		return elements.get(token);
	}

	/**
	 * Convenient method to call an element based on the page enclosed fields' enum
	 */
	public WebAutoElement getAutoElement(String token) {
		return autoElements.get(token);
	}

	public String getBeanClassName() {
		return beanClassName;
	}

	public void setBeanClassName(String beanClassName) {
		this.beanClassName = beanClassName;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public List<IWebElement> getLocationElements() {
		return new ArrayList<IWebElement>(elements.values());
	}

	/**
	 * set the driver that will be used by the automation elements
	 */
	public void setDriver(SynchronizedDriver sDvr) {
		for (WebAutoElement el : autoElements.values()) {
			el.setFrontEndDriver(sDvr);
		}
	}

}
