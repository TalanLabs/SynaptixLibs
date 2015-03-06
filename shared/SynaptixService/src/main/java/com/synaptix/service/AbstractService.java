package com.synaptix.service;

import com.synaptix.service.IService;
import com.synaptix.service.IServiceFactory;

public abstract class AbstractService implements IService {

	protected IServiceFactory serviceFactory;

	public void setServiceFactory(IServiceFactory serviceFactory) {
		this.serviceFactory = serviceFactory;
	}
}
