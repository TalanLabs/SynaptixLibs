package com.synaptix.deployer.client.model;

import com.synaptix.component.IComponent;

public interface IDatabaseQueryResult<T> extends IComponent {

	public T getResult();

	public void setResult(T result);

}
