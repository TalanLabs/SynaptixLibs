package com.synaptix.component.model;

import com.synaptix.component.IComponent;

public interface IError extends IComponent {

	@EqualsKey
	public ErrorEnum getErrorCode();

	public void setErrorCode(ErrorEnum getErrorCode);

	@EqualsKey
	public String getAttribute();

	public void setAttribute(String attribute);

	@EqualsKey
	public String getValue();

	public void setValue(String value);

}
