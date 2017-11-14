package com.synaptix.taskmanager.manager.taskservice;

import java.net.URI;
import java.util.Set;

import com.synaptix.component.model.IError;
import com.synaptix.component.model.IStackResult;
import com.synaptix.taskmanager.model.ITask;
import com.synaptix.taskmanager.model.ITaskObject;
import com.synaptix.taskmanager.model.domains.ServiceNature;

public interface ITaskService {

	/**
	 * Gets unique id code for the service (example: S0043)
	 *
	 * @return code as a String
	 */
	String getCode();

	ServiceNature getNature();

	String getCategory();

	String getDescription();

	boolean isCheckGenericEvent();

	void onTodo(ITask task);

	void onCurrent(ITask task);

	IExecutionResult execute(ITask task);

	void onDone(ITask task);

	void onSkipped(ITask task);

	void onCanceled(ITask task);

	ITodoDescriptor getTodoDescriptor();

	Class<? extends ITaskObject<?>> getObjectKinds();

	interface IContext {

	}

	interface IExecutionResult {

		boolean isFinished();

		String getErrorMessage();

		boolean hasErrors();

		Set<IError> getErrors();

		IStackResult getStackResult();

		String getResultStatus();

		String getResultDesc();

		boolean mustStopAndRestartTaskManager();
	}

	interface ITodoDescriptor {

		URI getUri();

		String getCode();

		String getDescription();

		boolean isCreateToTodoTask();

	}
}
