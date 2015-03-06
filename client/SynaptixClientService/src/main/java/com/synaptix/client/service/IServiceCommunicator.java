package com.synaptix.client.service;

import java.util.List;

public interface IServiceCommunicator {

	public abstract Object sendMessage(String serviceFactoryName,
			String serviceName, String methodName, Class<?>[] parameterTypes,
			Object[] args, Long timeout) throws Exception;

	public abstract List<ServiceDescriptor> findAllServiceDescriptorListBy(
			String serviceFactoryName) throws Exception;

	public final class ServiceDescriptor {

		private String scope;

		private String serviceClassName;

		private String description;

		public ServiceDescriptor(String scope, String serviceClassName,
				String description) {
			super();
			this.scope = scope;
			this.serviceClassName = serviceClassName;
			this.description = description;
		}

		public String getScope() {
			return scope;
		}

		public String getServiceClassName() {
			return serviceClassName;
		}

		public String getDescription() {
			return description;
		}
	}
}
