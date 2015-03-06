package com.synpatix.redpepper.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongo.test.domain.def.test.IBlock;
import com.mongo.test.domain.impl.test.TestPage;
import com.mongo.test.guice.MongoModule;
import com.mongo.test.service.dao.access.test.TestPageDaoService;
import com.mongo.test.service.dao.access.test.TestServiceFactory;
import com.synpatix.redpepper.backend.core.parse.TestParser;

public class MainClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Injector injector = Guice.createInjector(new MongoModule());
		// TestPageDaoService.Factory factory = injector.getInstance(TestPageDaoService.Factory.class);
		// TestPageDaoService testPageService = factory.create("test_project_db");
		// ProjectDaoService.Factory pfactory = injector.getInstance(ProjectDaoService.Factory.class);
		// ProjectDaoService projectService = pfactory.create("test_project_db");
		//
		// CampaignDaoService.Factory cfactory = injector.getInstance(CampaignDaoService.Factory.class);
		// CampaignDaoService campaignService = cfactory.create("test_project_db");

		// TestPage tPage = testPageService.getByName("Consignation.txt");
		// tPage.startExecution();
		// tPage = testPageService.saveAsNewIteration(tPage);
		//
		// Campaign c = new Campaign();
		// c.setTestCases(Arrays.asList(tPage));
		// c = campaignService.saveAsNewIteration(c);
		//
		// Project p = new Project();
		// p.setName("Test project");
		// p.setVersion("v2.0");
		// p.setCampaigns(Arrays.asList(c));

		// Project p = projectService.getByName("Test project");
		// projectService.saveNewIteration(p);
		// projectService.saveNewIteration(p);

		// List<Project> findAllIterationsByProjectName = projectService.findAllIterationsByProjectName("Test project", "v2.0");
		// System.out.println(findAllIterationsByProjectName.size());
		saveTestPageFromFile();
	}

	private static void saveTestPageFromFile() {
		Injector injector = Guice.createInjector(new MongoModule());
		TestPageDaoService.Factory factory = injector.getInstance(TestPageDaoService.Factory.class);
		TestPageDaoService testPageService = factory.create("test_project_db");
		TestServiceFactory tsFactory = injector.getInstance(TestServiceFactory.class);
		List<String> files = new ArrayList<String>();
		files.add("D:\\Projects\\psc\\PSC\\test\\PscTestGreenPepper\\src\\main\\resources\\TestFiles\\WKF_PR_OLD.txt");
		for (String fileName : files) {
			System.out.println("Start main test parser: " + fileName);
			// Read test file
			File file = new File(fileName);
			TestParser testParser = new TestParser();
			TestPage page = testParser.parse(file);
			page.setName("Consignation_Templare");
			page.setIsTemplate(true);
			List<IBlock> blocks = page.getBlocks();
			if (blocks != null && !blocks.isEmpty()) {
				for (IBlock b : blocks) {
					tsFactory.saveEntity(b); // vive mutability
				}
			}
			testPageService.save(page);
		}
	}
}
