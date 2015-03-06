package com.synaptix.server.service.extension;

import com.synaptix.sender.fax.IFaxFactory;
import com.synaptix.sender.mail.IMailFactory;
import com.synaptix.sender.sms.ISMSFactory;
import com.synaptix.server.service.extension.fax.IFaxFactoryService;
import com.synaptix.server.service.extension.mail.IMailFactoryService;
import com.synaptix.server.service.extension.sms.ISMSFactoryService;
import com.synaptix.service.AbstractService;

public abstract class AbstractAllFactoriesService extends AbstractService implements IMailFactoryService, ISMSFactoryService, IFaxFactoryService {

	protected IMailFactory mailFactory;

	protected IFaxFactory faxFactory;

	protected ISMSFactory smsFactory;

	public void setSMSFactory(ISMSFactory smsFactory) {
		this.smsFactory = smsFactory;
	}

	public void setFaxFactory(IFaxFactory faxFactory) {
		this.faxFactory = faxFactory;
	}

	public void setMailFactory(IMailFactory mailFactory) {
		this.mailFactory = mailFactory;
	}
}
