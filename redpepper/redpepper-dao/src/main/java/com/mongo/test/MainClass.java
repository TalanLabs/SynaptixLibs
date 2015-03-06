package com.mongo.test;

import com.google.inject.Injector;
import com.mongo.test.domain.impl.repository.WebElementImpl;
import com.mongo.test.service.dao.access.repository.WebElementDaoService;

public class MainClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

	private void testWebElements(Injector injector) {
		WebElementDaoService.Factory factory = injector.getInstance(WebElementDaoService.Factory.class);
		WebElementDaoService webElemenService = factory.create("test_project_db");
		System.out.println(webElemenService.count());
		for (WebElementImpl e : webElemenService.find().asList()) {
			System.out.println(e.getName());
		}
		System.out.println(webElemenService.count());
	}
}
