package com.synaptix.taskmanager.guice;

import com.synaptix.client.common.message.CommonConstantsBundle;
import com.synaptix.client.common.message.DateConstantsBundle;
import com.synaptix.guice.module.AbstractSynaptixConstantsBundleModule;
import com.synaptix.taskmanager.message.ErrorsTableConstantsBundle;
import com.synaptix.taskmanager.message.EventFollowupTaskManagerContextTableConstantsBundle;
import com.synaptix.taskmanager.message.StatusGraphTableConstantsBundle;
import com.synaptix.taskmanager.message.TaskChainTableConstantsBundle;
import com.synaptix.taskmanager.message.TaskHistoryTableConstantsBundle;
import com.synaptix.taskmanager.message.TaskManagerConstantsBundle;
import com.synaptix.taskmanager.message.TaskManagerModuleConstantsBundle;
import com.synaptix.taskmanager.message.TaskServiceDescriptorTableConstantsBundle;
import com.synaptix.taskmanager.message.TaskTableConstantsBundle;
import com.synaptix.taskmanager.message.TaskTypeTableConstantsBundle;
import com.synaptix.taskmanager.message.TodoFolderTableConstantsBundle;
import com.synaptix.taskmanager.message.TodoTableConstantsBundle;
import com.synaptix.taskmanager.message.TodosManagementConstantsBundle;
import com.synaptix.taskmanager.message.TransportRequestTaskManagerContextTableConstantsBundle;
import com.synaptix.widget.guice.SwingConstantsBundleManager;

public class TaskManagerConstantsBundleClientModule extends AbstractSynaptixConstantsBundleModule {

	public TaskManagerConstantsBundleClientModule() {
		super(SwingConstantsBundleManager.class);
	}

	@Override
	protected void configure() {
		bindConstantsBundle(TaskManagerModuleConstantsBundle.class);
		bindConstantsBundle(DateConstantsBundle.class);
		bindConstantsBundle(TaskManagerConstantsBundle.class);
		bindConstantsBundle(TaskServiceDescriptorTableConstantsBundle.class);
		bindConstantsBundle(TaskTypeTableConstantsBundle.class);
		bindConstantsBundle(TaskChainTableConstantsBundle.class);
		bindConstantsBundle(StatusGraphTableConstantsBundle.class);
		bindConstantsBundle(TaskTableConstantsBundle.class);
		bindConstantsBundle(TaskHistoryTableConstantsBundle.class);
		bindConstantsBundle(ErrorsTableConstantsBundle.class);
		bindConstantsBundle(TodoTableConstantsBundle.class);
		bindConstantsBundle(TodosManagementConstantsBundle.class);
		bindConstantsBundle(TodoFolderTableConstantsBundle.class);
		bindConstantsBundle(TransportRequestTaskManagerContextTableConstantsBundle.class);
		bindConstantsBundle(EventFollowupTaskManagerContextTableConstantsBundle.class);
		bindConstantsBundle(CommonConstantsBundle.class);
	}
}
