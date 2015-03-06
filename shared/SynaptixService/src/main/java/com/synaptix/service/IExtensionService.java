package com.synaptix.service;

import com.synaptix.service.IServiceFactory.ServiceDescriptor;

public interface IExtensionService {

	public abstract void setup(ServiceDescriptor serviceDescriptor,
			Object service);

}
