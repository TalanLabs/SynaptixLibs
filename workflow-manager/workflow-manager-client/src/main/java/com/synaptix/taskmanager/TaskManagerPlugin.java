package com.synaptix.taskmanager;

import org.java.plugin.Plugin;

import com.google.inject.Module;
import com.synaptix.client.common.IGuiceModule;
import com.synaptix.taskmanager.guice.TaskManagerClientModule;

public class TaskManagerPlugin extends Plugin implements IGuiceModule {

	public static final String PLUGIN_ID = "com.synaptix.taskmanager"; //$NON-NLS-1$

	@Override
	public Module createModule() {
		return new TaskManagerClientModule();
	}

	@Override
	protected void doStart() throws Exception {
	}

	@Override
	protected void doStop() throws Exception {
	}
}

