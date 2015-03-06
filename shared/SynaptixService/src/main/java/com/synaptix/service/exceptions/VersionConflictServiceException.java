package com.synaptix.service.exceptions;

import com.synaptix.service.ServiceException;

public class VersionConflictServiceException extends ServiceException {

	private static final long serialVersionUID = -2301047472385070179L;

	public VersionConflictServiceException() {
		super(null);
	}
}
