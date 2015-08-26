package com.synaptix.taskmanager.service.error;

import com.synaptix.component.model.ErrorEnum;
import com.synaptix.component.model.ErrorType;

public enum TaskManagerErrorEnum implements ErrorEnum {

	/***/
	CONFLICT(ErrorType.T, "Conflicting error"),
	/***/
	TASK(ErrorType.T, "Task error");

	private final ErrorType errorType;

	private final String label;

	private TaskManagerErrorEnum(ErrorType errorType, String label) {
		this.errorType = errorType;
		this.label = label;
	}

	@Override
	public ErrorType getType() {
		return errorType;
	}

	@Override
	public String getLabel() {
		return label;
	}
}
