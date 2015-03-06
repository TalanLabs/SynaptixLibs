package com.synaptix.service;

public interface IServiceFactory {

	public abstract String getScope();

	/**
	 * Get all service's descriptor
	 * 
	 * @return Array service's descriptor
	 */
	public abstract ServiceDescriptor[] getServiceDescriptors();

	/**
	 * Get a service's descriptor
	 * 
	 * @param type
	 * @return
	 */
	public abstract ServiceDescriptor getServiceDescriptor(Class<?> type);

	/**
	 * Get service
	 * 
	 * @param type
	 * @return service's object
	 * @exception NotFoundServiceException
	 */
	public abstract <T> T getService(Class<T> type);

	public abstract void addServiceInterceptor(IServiceInterceptor l);

	public abstract void removeServiceInterceptor(IServiceInterceptor l);

	public final class ServiceDescriptor {

		private String scope;

		private Class<?> serviceClass;

		private String description;

		public ServiceDescriptor(String scope, Class<?> serviceClass, String description) {
			super();
			this.scope = scope;
			this.serviceClass = serviceClass;
			this.description = description;
		}

		public String getScope() {
			return scope;
		}

		public Class<?> getServiceClass() {
			return serviceClass;
		}

		public String getDescription() {
			return description;
		}

		@Override
		public String toString() {
			return new StringBuilder("ServiceDescriptor@").append(this.hashCode()).append("[scope=").append(scope).append(", serviceClass=").append(serviceClass.getName()).append("]").toString();
		}
	}

}
