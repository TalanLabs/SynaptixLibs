package com.synaptix.deployer.client.controller;

import com.synaptix.deployer.model.ISynaptixDatabaseSchema;

public interface IDatabaseQueryContext<T> {

	public String getName();

	public String getMeaning();

	public T launchTest(ISynaptixDatabaseSchema database);

	public String getHTMLResult(T result);

	public boolean isValid(T result);

}
