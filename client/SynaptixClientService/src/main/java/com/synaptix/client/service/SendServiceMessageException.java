package com.synaptix.client.service;

public class SendServiceMessageException extends Exception {

	private static final long serialVersionUID = -2492745500978442384L;

	public enum Type {
		Timeout, Send, NotConnected, Other
	}

	private Type type;

	public SendServiceMessageException(Type type, Exception e) {
		super(e);

		this.type = type;
	}

	public SendServiceMessageException(Type type, String t) {
		super(t);

		this.type = type;
	}

	public Type getType() {
		return type;
	}

}
