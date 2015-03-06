package com.synaptix.smackx.service;

import java.util.Date;

import com.synaptix.client.service.IServiceMessageRecveid;

class ServiceMessageRecveid implements IServiceMessageRecveid {

	private String serviceFactoryName;

	private String serviceName;

	private String methodName;

	private Class<?>[] argTypes;

	private Object[] args;

	private Date startDate;

	private Date endDate;

	private String error;

	private Class<?> resultType;

	public String getServiceFactoryName() {
		return serviceFactoryName;
	}

	public void setServiceFactoryName(String serviceName) {
		this.serviceFactoryName = serviceName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String className) {
		this.serviceName = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?>[] getArgTypes() {
		return argTypes;
	}

	public void setArgTypes(Class<?>[] argTypes) {
		this.argTypes = argTypes;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Class<?> getResultType() {
		return resultType;
	}

	public void setResultType(Class<?> resultType) {
		this.resultType = resultType;
	}
}
