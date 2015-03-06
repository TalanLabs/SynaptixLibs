package com.synaptix.server.service.extension.mail;

import com.synaptix.sender.mail.IMailFactory;
import com.synaptix.service.AbstractService;

public abstract class AbstractMailFactoryService extends AbstractService
		implements IMailFactoryService {

	protected IMailFactory mailFactory;

	public void setMailFactory(IMailFactory mailFactory) {
		this.mailFactory = mailFactory;
	}
}
