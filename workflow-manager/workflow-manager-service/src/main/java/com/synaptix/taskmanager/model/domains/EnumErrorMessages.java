package com.synaptix.taskmanager.model.domains;

/**
 * Created by E413544 on 15/01/2015.
 */
public enum EnumErrorMessages {
	ERROR_MESSAGE_WAITING("Waiting..."),
	DEFAULT_ERROR_MESSAGE_LIST("See errors list");

	private String message;

	EnumErrorMessages(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
