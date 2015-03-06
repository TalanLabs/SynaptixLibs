package com.synaptix.server.service.extension.properties;

import com.synaptix.service.IExtensionService;
import com.synaptix.service.IServiceFactory.ServiceDescriptor;

public class PropetiesFactoryExtensionService implements IExtensionService {

	private IPropertiesFactory propertiesFactory;

	public PropetiesFactoryExtensionService(IPropertiesFactory propertiesFactory) {
		this.propertiesFactory = propertiesFactory;
	}

	public void setup(ServiceDescriptor serviceDescriptor, Object service) {
		if (service instanceof IPropertiesFactoryService) {
			((IPropertiesFactoryService) service)
					.setPropertiesFactory(propertiesFactory);
		}
	}
}
