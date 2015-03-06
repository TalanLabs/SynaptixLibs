package com.synaptix.downloader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IDownloaderProcess {

	public void process(HttpServletRequest req, HttpServletResponse resp) throws DownloaderProcessException;

}
