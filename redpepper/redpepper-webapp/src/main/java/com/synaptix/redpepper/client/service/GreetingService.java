package com.synaptix.redpepper.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.synaptix.redpepper.client.bean.ElementInfoDto;
import com.synaptix.redpepper.client.bean.PageInfoDto;
import com.synaptix.redpepper.client.bean.ProjectInfoDto;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {

	List<String> loadTestScripts(String selectedJar) throws IllegalArgumentException;

	String play(String selectedJar, String testName) throws IllegalArgumentException;

	List<PageInfoDto> loadPages(String selectedJar) throws IllegalArgumentException;

	void saveElementInfo(PageInfoDto currentPageInfo, ElementInfoDto elementDto) throws IllegalArgumentException;

	void loadBackendTests() throws IllegalArgumentException;

	String runBackendTest(String backendTest) throws IllegalArgumentException;

	List<String> listAllJarsInUri(String uri) throws IllegalArgumentException;

	List<ProjectInfoDto> listAllReports() throws IllegalArgumentException;
}
