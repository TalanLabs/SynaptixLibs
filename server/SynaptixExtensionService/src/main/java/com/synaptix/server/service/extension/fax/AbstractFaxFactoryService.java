package com.synaptix.server.service.extension.fax;

import com.synaptix.sender.fax.IFaxFactory;
import com.synaptix.service.AbstractService;

public abstract class AbstractFaxFactoryService extends AbstractService
		implements IFaxFactoryService {

	protected IFaxFactory faxFactory;

	public void setFaxFactory(IFaxFactory faxFactory) {
		this.faxFactory = faxFactory;
	}
}
