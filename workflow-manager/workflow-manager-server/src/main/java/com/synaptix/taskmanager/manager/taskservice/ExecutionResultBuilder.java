package com.synaptix.taskmanager.manager.taskservice;

import java.util.HashSet;
import java.util.Set;

import com.synaptix.component.model.IError;
import com.synaptix.component.model.IServiceResult;
import com.synaptix.component.model.IStackResult;
import com.synaptix.component.model.IServiceWithErrorResult;
import com.synaptix.taskmanager.model.domains.EnumErrorMessages;

public class ExecutionResultBuilder {

	private final ExecutionResultImpl executionResultImpl;

	public ExecutionResultBuilder() {
		super();

		this.executionResultImpl = new ExecutionResultImpl();
	}

	/**
	 * Will save the errors contained in the serviceResult
	 */
	public <O> ExecutionResultBuilder serviceResult(IServiceResult<O> serviceResult) {
		if ((serviceResult != null) && (IServiceWithErrorResult.class.isAssignableFrom(serviceResult.getClass()))) {
			if (serviceResult.hasError()) {
				errors(((IServiceWithErrorResult<O>) serviceResult).getErrorSet());
			}
			stackResult(serviceResult.getStackResult());
		}
		return this;
	}

	public ExecutionResultBuilder errorMessage(String errorMessage) {
		executionResultImpl.errorMessage = errorMessage;
		return this;
	}

	public ITaskService.IExecutionResult finished() {
		executionResultImpl.finished = true;
		return executionResultImpl;
	}

	public ITaskService.IExecutionResult notFinished() {
		executionResultImpl.finished = false;
		return executionResultImpl;
	}

	public ExecutionResultBuilder errors(Set<IError> errors) {
		executionResultImpl.errors = errors;
		return this;
	}

	public ExecutionResultBuilder stackResult(IStackResult stackResult) {
		executionResultImpl.stackResult = stackResult;
		return this;
	}

	public ExecutionResultBuilder resultStatus(String resultStatus) {
		executionResultImpl.resultStatus = resultStatus;
		return this;
	}

	/**
	 * Sets the result desc. If null, uses the resultDesc of the first stack
	 */
	public ExecutionResultBuilder resultDesc(String resultDesc) {
		executionResultImpl.resultDesc = resultDesc;
		return this;
	}

	/**
	 * Set to true if object cluster has changed. Task manager will stop at end of task, and restart on new object cluster.
	 */
	public ExecutionResultBuilder mustStopAndRestartTaskManager(boolean b) {
		executionResultImpl.mustStopAndRestartTaskManager = b;
		return this;
	}

	public ITaskService.IExecutionResult waiting() {
		return errorMessage(EnumErrorMessages.ERROR_MESSAGE_WAITING.getMessage()).notFinished();
	}

	public static final class ExecutionResultImpl implements ITaskService.IExecutionResult {

		private String errorMessage;

		private boolean finished;

		private Set<IError> errors;

		private IStackResult stackResult;

		private String resultStatus;

		private String resultDesc;

		private boolean mustStopAndRestartTaskManager;

		public ExecutionResultImpl() {
			super();
			errors = new HashSet<IError>();
		}

		@Override
		public String getErrorMessage() {
			return errorMessage;
		}

		@Override
		public boolean isFinished() {
			return finished;
		}

		@Override
		public Set<IError> getErrors() {
			return errors;
		}

		@Override
		public boolean hasErrors() {
			return !errors.isEmpty();
		}

		@Override
		public IStackResult getStackResult() {
			return stackResult;
		}

		@Override
		public String getResultStatus() {
			return resultStatus;
		}

		@Override
		public String getResultDesc() {
			return resultDesc;
		}

		@Override
		public boolean mustStopAndRestartTaskManager() {
			return mustStopAndRestartTaskManager;
		}
	}
}
