package com.synaptix.component.model;

import java.util.Set;

/**
 * Full interface to describe a service result with its errors and result status, text, ...<br/>
 * This result status has its own children.
 *
 * @author Nicolas P
 */
public interface IServiceWithErrorResult<O extends Object> extends IServiceResult<O> {

	Set<IError> getErrorSet();

	void setErrorSet(Set<IError> errorSet);

	// Getter is in IServiceResult
	void setStackResult(IStackResult stackResult);

	// Getter is in IServiceResult
	void setObject(O object);

	// Getter is in IServiceResult
	void setError(boolean hasError);

}
