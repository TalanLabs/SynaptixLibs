package com.synaptix.service;

public abstract class AbstractService implements IService {

	protected IServiceFactory serviceFactory;

	public void setServiceFactory(IServiceFactory serviceFactory) {
		this.serviceFactory = serviceFactory;
	}
}
