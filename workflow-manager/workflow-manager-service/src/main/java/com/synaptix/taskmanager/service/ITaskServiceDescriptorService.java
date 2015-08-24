package com.synaptix.taskmanager.service;

import com.synaptix.service.IPaginationService;

import com.synaptix.taskmanager.model.ITaskServiceDescriptor;

public interface ITaskServiceDescriptorService extends IPaginationService<ITaskServiceDescriptor> {

	public ITaskServiceDescriptor findTaskServiceDescriptorByCode(String serviceCode);

}
