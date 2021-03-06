package com.synaptix.component.model;

import java.util.Set;

/**
 * The lightest object which contains an object.<br/>
 * Setter is in {@link IServiceWithErrorResult}
 *
 * @author Nicolas P
 */
public interface IServiceResult<O extends Object> {

	O getObject();

	// Setter is in IServiceWithErrorResult
	boolean hasError();

	IStackResult getStackResult();

	/**
	 * Returns the set of errors (immutable if done correctly)
	 */
	Set<IError> getErrors();

	String getResultCode();

	String getResultText();

}
