package com.synaptix.mybatis.dao.exceptions;

public class DaoException extends RuntimeException {

	private static final long serialVersionUID = -354266025672687706L;

	public DaoException() {
		super();
	}

	public DaoException(String message) {
		super(message);
	}

	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}

	public DaoException(Throwable cause) {
		super(cause);
	}
}
