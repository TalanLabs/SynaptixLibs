package com.synaptix.server.service.extension.sms;

import com.synaptix.sender.sms.ISMSFactory;
import com.synaptix.service.IExtensionService;
import com.synaptix.service.IServiceFactory.ServiceDescriptor;

public class SMSFactoryExtensionService implements IExtensionService {

	private ISMSFactory smsFactory;

	public SMSFactoryExtensionService(ISMSFactory smsFactory) {
		this.smsFactory = smsFactory;
	}

	public void setup(ServiceDescriptor serviceDescriptor, Object service) {
		if (service instanceof ISMSFactoryService) {
			((ISMSFactoryService) service).setSMSFactory(smsFactory);
		}
	}
}
