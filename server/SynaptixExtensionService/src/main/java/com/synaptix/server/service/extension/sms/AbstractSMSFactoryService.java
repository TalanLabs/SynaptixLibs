package com.synaptix.server.service.extension.sms;

import com.synaptix.sender.sms.ISMSFactory;
import com.synaptix.service.AbstractService;

public abstract class AbstractSMSFactoryService extends AbstractService
		implements ISMSFactoryService {

	protected ISMSFactory smsFactory;

	public void setSMSFactory(ISMSFactory smsFactory) {
		this.smsFactory = smsFactory;
	}
}
