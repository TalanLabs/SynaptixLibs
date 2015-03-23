package com.synaptix.service.exceptions;

import com.synaptix.service.ServiceException;

/**
 * Exception raised by a service when trying to save an entity with a different version than the one given
 */
public class VersionConflictServiceException extends ServiceException {

	private static final long serialVersionUID = -2301047472385070179L;

	public VersionConflictServiceException() {
		super(null);
	}
}
