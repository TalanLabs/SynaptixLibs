package com.synaptix.redpepper.automation.repository.source;

import java.io.InputStream;

public interface ISourceConnector {

	public String[] getResourceListing(String path);

	public InputStream getFileStream(String filePath);
}
