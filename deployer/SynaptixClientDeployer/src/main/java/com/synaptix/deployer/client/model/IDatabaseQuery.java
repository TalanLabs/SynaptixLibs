package com.synaptix.deployer.client.model;

import com.synaptix.component.IComponent;

public interface IDatabaseQuery extends IComponent {

	public String getName();

	public void setName(String name);

	public String getMeaning();

	public void setMeaning(String name);

	public Boolean isValid();

	public void setValid(Boolean valid);

}
