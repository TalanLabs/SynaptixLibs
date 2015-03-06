package com.synaptix.server.service.extension.properties;

import com.synaptix.service.AbstractService;

public abstract class AbstractPropertiesFactoryService extends AbstractService
		implements IPropertiesFactoryService {

	protected IPropertiesFactory propertiesFactory;

	public void setPropertiesFactory(IPropertiesFactory propertiesFactory) {
		this.propertiesFactory = propertiesFactory;
	}
}
