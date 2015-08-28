package com.synaptix.taskmanager.guice.child;

import com.google.inject.Module;
import com.google.inject.Singleton;
import com.synaptix.server.service.guice.AbstractSynaptixServerServiceModule;
import com.synaptix.taskmanager.delegate.TaskManagerServiceDelegate;
import com.synaptix.taskmanager.manager.TaskServiceDiscovery;
import com.synaptix.taskmanager.service.IStatusGraphService;
import com.synaptix.taskmanager.service.ITaskChainCriteriaService;
import com.synaptix.taskmanager.service.ITaskChainService;
import com.synaptix.taskmanager.service.ITaskManagerService;
import com.synaptix.taskmanager.service.ITaskServiceDescriptorService;
import com.synaptix.taskmanager.service.ITasksService;
import com.synaptix.taskmanager.service.StatusGraphServerService;
import com.synaptix.taskmanager.service.TaskChainCriteriaServerService;
import com.synaptix.taskmanager.service.TaskChainServerService;
import com.synaptix.taskmanager.service.TaskManagerServerService;
import com.synaptix.taskmanager.service.TaskServiceDescriptorServerService;
import com.synaptix.taskmanager.service.TasksServerService;

public class ServerServiceTaskManagerModule extends AbstractSynaptixServerServiceModule implements Module {

	@Override
	protected void configure() {
		bind(TaskServiceDiscovery.class).in(Singleton.class);

		bindService(TaskManagerServerService.class).with(ITaskManagerService.class);
		bindService(TaskServiceDescriptorServerService.class).with(ITaskServiceDescriptorService.class);
		bindService(TaskChainServerService.class).with(ITaskChainService.class);
		bindService(StatusGraphServerService.class).with(IStatusGraphService.class);
		bindService(TasksServerService.class).with(ITasksService.class);
		bindService(TaskChainCriteriaServerService.class).with(ITaskChainCriteriaService.class);

		bindDelegate(TaskManagerServiceDelegate.class);
	}
}
