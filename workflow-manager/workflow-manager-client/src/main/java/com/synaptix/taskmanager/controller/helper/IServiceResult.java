package com.synaptix.taskmanager.controller.helper;

/**
 * The lightest object which contains an object.<br/>
 * Setter is in {@link IServiceWithErrorResult}
 *
 * @author Nicolas P
 */
public interface IServiceResult<O extends Object> {

	public O getObject();

	// Setter is in IServiceWithErrorResult
	public boolean hasError();

	public IStackResult getStackResult();

}
