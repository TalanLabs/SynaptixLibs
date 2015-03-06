package com.synaptix.server.service.extension.version;

import com.synaptix.service.IExtensionService;
import com.synaptix.service.IServiceFactory.ServiceDescriptor;

public class VersionFactoryExtensionService implements IExtensionService {

	private String versionString;

	public VersionFactoryExtensionService(String versionString) {
		this.versionString = versionString;
	}

	public void setup(ServiceDescriptor serviceDescriptor, Object service) {
		if (service instanceof IVersionFactoryService) {
			((IVersionFactoryService) service).setVersion(versionString);
		}
	}
}
