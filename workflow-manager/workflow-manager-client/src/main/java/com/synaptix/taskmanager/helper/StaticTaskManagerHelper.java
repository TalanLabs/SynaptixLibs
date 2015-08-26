package com.synaptix.taskmanager.helper;

import com.synaptix.auth.AuthsBundleManager;
import com.synaptix.taskmanager.auth.TaskManagerAuthsBundle;
import com.synaptix.taskmanager.message.TaskManagerModuleConstantsBundle;
import com.synaptix.widget.util.AbstractStaticHelper;

public class StaticTaskManagerHelper extends AbstractStaticHelper {

	public static TaskManagerAuthsBundle getTaskManagerAuthsBundle() {
		return AuthsBundleManager.getInstance().getBundle(TaskManagerAuthsBundle.class);
	}

	public static TaskManagerModuleConstantsBundle getTaskManagerModuleConstantsBundle() {
		return getConstantsBundleManager().getBundle(TaskManagerModuleConstantsBundle.class);
	}
}
