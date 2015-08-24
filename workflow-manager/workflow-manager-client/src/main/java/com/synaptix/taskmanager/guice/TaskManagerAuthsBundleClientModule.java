package com.synaptix.taskmanager.guice;

import com.synaptix.client.common.message.CommonAuthsBundle;
import com.synaptix.guice.module.AbstractSynaptixAuthsBundleModule;
import com.synaptix.taskmanager.auth.TaskManagerAuthsBundle;

public class TaskManagerAuthsBundleClientModule extends AbstractSynaptixAuthsBundleModule {

	public TaskManagerAuthsBundleClientModule() {
		super();
	}

	@Override
	protected void configure() {
		bindAuthsBundle(TaskManagerAuthsBundle.class);
		bindAuthsBundle(CommonAuthsBundle.class);
	}
}
