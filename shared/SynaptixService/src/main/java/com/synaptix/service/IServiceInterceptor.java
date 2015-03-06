package com.synaptix.service;

public interface IServiceInterceptor {

	public void interceptService(String serviceName, String methodName,
			Class<?>[] argTypes, Object[] args);

}
