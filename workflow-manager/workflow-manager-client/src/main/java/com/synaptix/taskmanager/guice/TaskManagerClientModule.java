package com.synaptix.taskmanager.guice;

import com.google.inject.Singleton;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.taskmanager.controller.ITaskManagerController;
import com.synaptix.taskmanager.controller.TaskManagerController;
import com.synaptix.taskmanager.helper.StaticTaskManagerHelper;
import com.synaptix.taskmanager.helper.TaskManagerHelper;
import com.synaptix.taskmanager.helper.TaskManagerViewHelper;
import com.synaptix.taskmanager.helper.TaskObjectClassViewHelper;
import com.synaptix.taskmanager.objecttype.DefaultObjectTypeManager;
import com.synaptix.taskmanager.objecttype.DefaultURIClientManagerDiscovery;
import com.synaptix.taskmanager.objecttype.IObjectTypeManager;
import com.synaptix.taskmanager.urimanager.IURIClientManagerDiscovery;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.taskmanager.view.ITaskManagerViewFactory;
import com.synaptix.taskmanager.view.swing.SwingTaskManagerViewFactory;

public class TaskManagerClientModule extends AbstractClientModule {

	@Override
	protected void configure() {
		install(new TaskManagerConstantsBundleClientModule());
		install(new TaskManagerAuthsBundleClientModule());

		requestStaticInjection(StaticCommonHelper.class);
		requestStaticInjection(StaticHelper.class);
		requestStaticInjection(StaticTaskManagerHelper.class);

		requestStaticInjection(TaskManagerViewHelper.class);
		requestStaticInjection(TaskObjectClassViewHelper.class);
		requestStaticInjection(TaskManagerHelper.class);

		bind(SwingTaskManagerViewFactory.class).in(Singleton.class);
		bind(ITaskManagerViewFactory.class).to(SwingTaskManagerViewFactory.class).in(Singleton.class);

		bind(TaskManagerController.class).in(Singleton.class);
		bind(ITaskManagerController.class).to(TaskManagerController.class).in(Singleton.class);

		bind(IObjectTypeManager.class).to(DefaultObjectTypeManager.class).in(Singleton.class);
		bind(IURIClientManagerDiscovery.class).to(DefaultURIClientManagerDiscovery.class).in(Singleton.class);

		install(new TaskChainModule());
	}
}
