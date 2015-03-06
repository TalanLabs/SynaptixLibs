package com.synaptix.redpepper.server.guice;

import java.util.HashMap;
import java.util.Map;

import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.mongo.test.guice.MongoModule;
import com.synaptix.redpepper.server.GreetingServiceImpl;
import com.synaptix.redpepper.server.servlet.ProjectServlet;
import com.synaptix.redpepper.server.servlet.TestReportServlet;

public class BootServerServletModule extends ServletModule {

	@Override
	protected void configureServlets() {
		install(new MongoModule());

		serve("/redpepper/greet").with(GreetingServiceImpl.class);
		serve("/reports").with(ProjectServlet.class);
		serve("/test").with(TestReportServlet.class);
		// serve("/" + ActionImpl.DEFAULT_SERVICE_NAME + "*").with(DispatchServiceImpl.class);

		bind(HttpServletDispatcher.class).in(Singleton.class);
		Map<String, String> restParams = new HashMap<String, String>();
		restParams.put("resteasy.servlet.mapping.prefix", "/rest");
		serve("/rest/*").with(HttpServletDispatcher.class, restParams);

	}
}
