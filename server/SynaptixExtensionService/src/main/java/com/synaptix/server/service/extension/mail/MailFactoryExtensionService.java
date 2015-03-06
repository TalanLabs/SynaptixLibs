package com.synaptix.server.service.extension.mail;

import com.synaptix.sender.mail.IMailFactory;
import com.synaptix.service.IExtensionService;
import com.synaptix.service.IServiceFactory.ServiceDescriptor;

public class MailFactoryExtensionService implements IExtensionService {

	private IMailFactory mailFactory;

	public MailFactoryExtensionService(IMailFactory mailFactory) {
		this.mailFactory = mailFactory;
	}

	public void setup(ServiceDescriptor serviceDescriptor, Object service) {
		if (service instanceof IMailFactoryService) {
			((IMailFactoryService) service).setMailFactory(mailFactory);
		}
	}
}
