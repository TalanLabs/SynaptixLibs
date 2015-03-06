package com.synaptix.server.service.extension.fax;

import com.synaptix.sender.fax.IFaxFactory;
import com.synaptix.service.IExtensionService;
import com.synaptix.service.IServiceFactory.ServiceDescriptor;

public class FaxFactoryExtensionService implements IExtensionService {

	private IFaxFactory faxFactory;

	public FaxFactoryExtensionService(IFaxFactory faxFactory) {
		this.faxFactory = faxFactory;
	}

	public void setup(ServiceDescriptor serviceDescriptor, Object service) {
		if (service instanceof IFaxFactoryService) {
			((IFaxFactoryService) service).setFaxFactory(faxFactory);
		}
	}
}
