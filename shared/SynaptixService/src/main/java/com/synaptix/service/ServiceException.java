package com.synaptix.service;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = -1897553093423145096L;

	private String code;

	private String description;

	public ServiceException(Throwable cause) {
		this(null, cause);
	}

	// public ServiceException(String code) {
	// this(code, null, null);
	// }
	//
	public ServiceException(String code, Throwable cause) {
		this(code, null, cause);
	}

	public ServiceException(String code, String description, Throwable cause) {
		super(code, cause);

		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String toString() {
		String s = getClass().getName();
		String message = getLocalizedMessage();
		return (message != null ? (s + ": " + message) : s) + (description != null ? (" -> " + description) : "");
	}
}
