package com.synaptix.server.service.extension.file;

import com.synaptix.sender.file.IFileFactory;
import com.synaptix.service.AbstractService;

public abstract class AbstractFileFactoryService extends AbstractService
		implements IFileFactoryService {

	protected IFileFactory fileFactory;

	public void setFileFactory(IFileFactory fileFactory) {
		this.fileFactory = fileFactory;
	}
}
