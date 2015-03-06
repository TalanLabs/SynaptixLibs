package com.synaptix.redpepper.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongo.test.domain.impl.report.Campaign;
import com.mongo.test.domain.impl.report.Project;
import com.mongo.test.domain.impl.test.TestPage;
import com.mongo.test.service.dao.access.project.ProjectDaoService;
import com.synpatix.redpepper.backend.core.report.ProjectHtmlReportGenerator;

@Singleton
public class TestReportServlet extends HttpServlet {

	ProjectDaoService projectService;

	@Inject
	public TestReportServlet(ProjectDaoService.Factory pfactory) {
		super();
		projectService = pfactory.create("test_project_db");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String pName = req.getParameter("project");
			String iter = req.getParameter("iteration");
			String tName = req.getParameter("test");
			Project p = projectService.getByNameAndIteration(pName, iter);
			TestPage testPage = null;
			for (Campaign c : p.getCampaigns()) {
				for (TestPage tp : c.getTestCases()) {
					if (tName.equals(tp.getName())) {
						testPage = tp;
						break;
					}
				}
				if (testPage != null) {
					break;
				}
			}
			String pageReport = ProjectHtmlReportGenerator.generatePageReport(null, testPage);
			resp.setContentType("text/html");
			PrintWriter out = resp.getWriter();
			out.write(pageReport);
			out.close();
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
