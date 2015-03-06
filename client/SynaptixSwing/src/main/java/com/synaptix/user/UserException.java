package com.synaptix.user;

public class UserException extends Exception {

	private static final long serialVersionUID = 2854399490337749731L;

	public enum Type {
		REJECTED, WRONG_USERNAME, WRONG_PASSWORD, ERROR_CONNECTION
	};

	private Type type;

	private String login;

	private char[] password;

	public UserException(Type type, String login, char[] password) {
		this(type, login, password, null);
	}

	public UserException(Type type, String login, char[] password, Exception e) {
		super(type.name(), e);

		this.type = type;
		this.login = login;
		this.password = password;
	}

	public Type getType() {
		return type;
	}

	public String getLogin() {
		return login;
	}

	public char[] getPassword() {
		return password;
	}
}
