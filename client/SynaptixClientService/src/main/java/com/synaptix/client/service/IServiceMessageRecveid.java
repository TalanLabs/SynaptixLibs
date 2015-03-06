package com.synaptix.client.service;

import java.util.Date;

public interface IServiceMessageRecveid {

	public abstract String getServiceFactoryName();

	public abstract String getServiceName();

	public abstract String getMethodName();

	public abstract Class<?>[] getArgTypes();

	public abstract Object[] getArgs();

	public abstract Date getStartDate();

	public abstract Date getEndDate();

	public abstract String getError();

	public abstract Class<?> getResultType();

}