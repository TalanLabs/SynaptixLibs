package com.synaptix.server.service.extension.file;

import com.synaptix.sender.file.IFileFactory;
import com.synaptix.service.IExtensionService;
import com.synaptix.service.IServiceFactory.ServiceDescriptor;

public class FileFactoryExtensionService implements IExtensionService {

	private IFileFactory fileFactory;

	public FileFactoryExtensionService(IFileFactory fileFactory) {
		this.fileFactory = fileFactory;
	}

	public void setup(ServiceDescriptor serviceDescriptor, Object service) {
		if (service instanceof IFileFactoryService) {
			((IFileFactoryService) service).setFileFactory(fileFactory);
		}
	}
}
