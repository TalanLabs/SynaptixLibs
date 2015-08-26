package com.synaptix.taskmanager.controller.helper;

import java.util.Set;

import com.synaptix.component.model.IError;

/**
 * Full interface to describe a service result with its errors and result status, text, ...<br/>
 * This result status has its own children.
 *
 * @author Nicolas P
 */
public interface IServiceWithErrorResult<O extends Object> extends IServiceResult<O> {

	public Set<IError> getErrorSet();

	public void setErrorSet(Set<IError> errorSet);

	// Getter is in IServiceResult
	public void setStackResult(IStackResult stackResult);

	// Getter is in IServiceResult
	public void setObject(O object);

	// Getter is in IServiceResult
	public void setError(boolean hasError);

}
