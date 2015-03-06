package com.synaptix.server.service.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HTTPServerServiceServlet extends HttpServlet {

	private static final long serialVersionUID = 6580921516347719202L;

	private static final Log log = LogFactory
			.getLog(HTTPServerServiceServlet.class);

	public void init() throws ServletException {

	}
	
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ServletContext sc = getServletContext();

		try {
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);

			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}
