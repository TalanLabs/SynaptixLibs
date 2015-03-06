package com.synaptix.downloader.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;
import com.synaptix.downloader.DownloaderProcessException;
import com.synaptix.downloader.IDownloaderProcess;
import com.synaptix.downloader.IDownloaderRegistry;

public class DownloaderServlet extends HttpServlet {

	private static final long serialVersionUID = -2304959085237893786L;

	private static final Log LOG = LogFactory.getLog(DownloaderServlet.class);

	private final IDownloaderRegistry downloaderRegistry;

	@Inject
	public DownloaderServlet(IDownloaderRegistry downloaderRegistry) {
		super();
		this.downloaderRegistry = downloaderRegistry;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String key = req.getParameter("key");
		if (key != null) {
			IDownloaderProcess downloaderProcess = downloaderRegistry.get(key);
			if (downloaderProcess != null) {
				try {
					downloaderProcess.process(req, resp);
				} catch (DownloaderProcessException e) {
					LOG.error(e.getMessage(), e);
					sendInternalServerError(resp);
				}
			} else {
				sendNoContentError(resp);
			}
		} else {
			sendNoContentError(resp);
		}
	}

	private void sendNoContentError(HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_NO_CONTENT);
	}

	private void sendInternalServerError(HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
}
