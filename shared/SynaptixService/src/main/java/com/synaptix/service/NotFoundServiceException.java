package com.synaptix.service;

public class NotFoundServiceException extends RuntimeException {

	private static final long serialVersionUID = 7277450109916061967L;

	public NotFoundServiceException(String m) {
		super(m);
	}
}
