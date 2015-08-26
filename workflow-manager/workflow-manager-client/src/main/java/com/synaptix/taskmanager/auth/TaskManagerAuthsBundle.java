package com.synaptix.taskmanager.auth;

import com.synaptix.auth.AuthsBundle;

public interface TaskManagerAuthsBundle extends AuthsBundle {

	@Key(object = "taskServicesManagement", action = "read")
	@Description("Read task services")
	public boolean hasReadTaskServicesManagement();

	@Key(object = "taskTypesManagement", action = "read")
	@Description("Read task types")
	public boolean hasReadTaskTypesManagement();

	@Key(object = "taskTypesManagement", action = "write")
	@Description("Write task types")
	public boolean hasWriteTaskTypesManagement();

	@Key(object = "taskChainsManagement", action = "read")
	@Description("Read task chains")
	public boolean hasReadTaskChainsManagement();

	@Key(object = "taskChainsManagement", action = "write")
	@Description("Write task chains")
	public boolean hasWriteTaskChainsManagement();

	@Key(object = "statusGraphsManagement", action = "read")
	@Description("Read status graphs")
	public boolean hasReadStatusGraphsManagement();

	@Key(object = "statusGraphsManagement", action = "write")
	@Description("Write status graphs")
	public boolean hasWriteStatusGraphsManagement();

	@Key(object = "tasksManagement", action = "read")
	@Description("Read tasks")
	public boolean hasReadTasksManagement();

	@Key(object = "tasksManagement", action = "write")
	@Description("Write tasks")
	public boolean hasWriteTasksManagement();

	@Key(object = "errorsManagement", action = "read")
	@Description("Read errors")
	public boolean hasReadErrorsManagement();

	@Key(object = "errorsManagement", action = "write")
	@Description("Write errors")
	public boolean hasWriteErrorsManagement();

	@Key(object = "todosManagement", action = "read")
	@Description("Read a view for todos management")
	public boolean hasReadTodosManagement();

	@Key(object = "todoFoldersManagement", action = "read")
	@Description("View todo folders management")
	public boolean hasReadTodoFoldersManagement();

	@Key(object = "todoFoldersManagement", action = "write")
	@Description("Create and edit todo folders")
	public boolean hasWriteTodoFoldersManagement();

}
