package com.synaptix.redpepper.automation.repository;

import com.synaptix.redpepper.automation.elements.facade.IWebElement;
import com.synaptix.redpepper.automation.elements.impl.AbstractSynaptixWebPage;
import com.synaptix.redpepper.automation.repository.xml.XMLWebRepository;

public class WikificationUtils {

	/**
	 * @param args
	 */

	public static void main(String[] args) {
		XMLWebRepository repo = new XMLWebRepository();
		System.out.println("|| auto setup ||");
		for (AbstractSynaptixWebPage page : repo.getPages()) {
			System.out.println("| web page | " + page.getPageName() + " |");
			System.out.println("| name | type \\ | locator | method | position |");
			for (IWebElement element : page.getLocationElements()) {
				String name = element.getName();
				String type = element.getType().name();
				String locator = element.getLocator();
				String method = element.getMethod().name();
				int position = element.getPosition();
				System.out.println("| " + name + " | " + type + " \\ | " + locator + " | " + method + " | " + position + " |");
			}

			System.out.println("\n");
		}
	}
}
