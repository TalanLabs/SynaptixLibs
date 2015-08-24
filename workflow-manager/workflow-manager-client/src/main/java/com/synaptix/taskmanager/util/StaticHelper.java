package com.synaptix.taskmanager.util;

import com.synaptix.taskmanager.message.ErrorsTableConstantsBundle;
import com.synaptix.taskmanager.message.EventFollowupTaskManagerContextTableConstantsBundle;
import com.synaptix.taskmanager.message.StatusGraphTableConstantsBundle;
import com.synaptix.taskmanager.message.TaskChainTableConstantsBundle;
import com.synaptix.taskmanager.message.TaskHistoryTableConstantsBundle;
import com.synaptix.taskmanager.message.TaskManagerConstantsBundle;
import com.synaptix.taskmanager.message.TaskServiceDescriptorTableConstantsBundle;
import com.synaptix.taskmanager.message.TaskTableConstantsBundle;
import com.synaptix.taskmanager.message.TaskTypeTableConstantsBundle;
import com.synaptix.taskmanager.message.TodoFolderTableConstantsBundle;
import com.synaptix.taskmanager.message.TodoTableConstantsBundle;
import com.synaptix.taskmanager.message.TodosManagementConstantsBundle;
import com.synaptix.taskmanager.message.TransportRequestTaskManagerContextTableConstantsBundle;
import com.synaptix.widget.util.AbstractStaticHelper;

public class StaticHelper extends AbstractStaticHelper {

	public static final TaskManagerConstantsBundle getTaskManagerConstantsBundle() {
		return getConstantsBundleManager().getBundle(TaskManagerConstantsBundle.class);
	}

	public static final TaskServiceDescriptorTableConstantsBundle getTaskServiceDescriptorTableConstantsBundle() {
		return getConstantsBundleManager().getBundle(TaskServiceDescriptorTableConstantsBundle.class);
	}

	public static final TaskTypeTableConstantsBundle getTaskTypeTableConstantsBundle() {
		return getConstantsBundleManager().getBundle(TaskTypeTableConstantsBundle.class);
	}

	public static final TaskChainTableConstantsBundle getTaskChainTableConstantsBundle() {
		return getConstantsBundleManager().getBundle(TaskChainTableConstantsBundle.class);
	}

	public static final StatusGraphTableConstantsBundle getStatusGraphTableConstantsBundle() {
		return getConstantsBundleManager().getBundle(StatusGraphTableConstantsBundle.class);
	}

	public static final TaskTableConstantsBundle getTaskTableConstantsBundle() {
		return getConstantsBundleManager().getBundle(TaskTableConstantsBundle.class);
	}

	public static final TaskHistoryTableConstantsBundle getTaskHistoryTableConstantsBundle() {
		return getConstantsBundleManager().getBundle(TaskHistoryTableConstantsBundle.class);
	}

	public static ErrorsTableConstantsBundle getErrorsTableConstantsBundle() {
		return getConstantsBundleManager().getBundle(ErrorsTableConstantsBundle.class);
	}

	public static final TodoTableConstantsBundle getTodoTableConstantsBundle() {
		return getConstantsBundleManager().getBundle(TodoTableConstantsBundle.class);
	}

	public static TodoFolderTableConstantsBundle getTodoFoldersTableConstantsBundle() {
		return getConstantsBundleManager().getBundle(TodoFolderTableConstantsBundle.class);
	}

	public static TransportRequestTaskManagerContextTableConstantsBundle getTransportRequestTaskManagerContextTableConstantsBundle() {
		return getConstantsBundleManager().getBundle(TransportRequestTaskManagerContextTableConstantsBundle.class);
	}

	public static EventFollowupTaskManagerContextTableConstantsBundle getEventFollowupTaskManagerContextTableConstantsBundle() {
		return getConstantsBundleManager().getBundle(EventFollowupTaskManagerContextTableConstantsBundle.class);
	}

	public static TodosManagementConstantsBundle getTodosManagementConstantsBundle() {
		return getConstantsBundleManager().getBundle(TodosManagementConstantsBundle.class);
	}

}
