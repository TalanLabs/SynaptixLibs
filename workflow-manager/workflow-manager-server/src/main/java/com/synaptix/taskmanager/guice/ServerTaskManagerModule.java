package com.synaptix.taskmanager.guice;

import com.google.inject.AbstractModule;
import com.synaptix.taskmanager.guice.child.MyBatisTaskManagerModule;
import com.synaptix.taskmanager.guice.child.ServerServiceTaskManagerModule;

public class ServerTaskManagerModule extends AbstractModule {

	@Override
	protected void configure() {
		install(new ServerServiceTaskManagerModule());
		install(new MyBatisTaskManagerModule());
	}
}
