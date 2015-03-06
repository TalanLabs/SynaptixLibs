package com.synaptix.service.model;

import com.synaptix.component.IComponent;

public interface ISortOrder extends IComponent {

	@EqualsKey
	public String getPropertyName();

	public void setPropertyName(String propertyName);

	@EqualsKey
	public Boolean isAscending();

	public void setAscending(Boolean ascending);

}
