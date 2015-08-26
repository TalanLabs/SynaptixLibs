package com.synaptix.taskmanager.controller;

import com.synaptix.taskmanager.controller.helper.AbstractWorkflowComponentsManagementController;
import com.synaptix.taskmanager.model.ITaskServiceDescriptor;
import com.synaptix.taskmanager.service.ITaskServiceDescriptorService;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.widget.component.view.IComponentsManagementViewDescriptor;


public class TaskServiceDescriptorsManagementController extends AbstractWorkflowComponentsManagementController<ITaskManagerViewFactory, ITaskServiceDescriptor> {

	public TaskServiceDescriptorsManagementController(ITaskManagerViewFactory viewFactory) {
		super(viewFactory, ITaskServiceDescriptor.class, ITaskServiceDescriptorService.class);
	}

	@Override
	protected IComponentsManagementViewDescriptor<ITaskServiceDescriptor> createComponentsManagementViewDescriptor() {
		return getViewFactory().createTaskServiceDescriptorViewDescriptor(this);
	}
}
