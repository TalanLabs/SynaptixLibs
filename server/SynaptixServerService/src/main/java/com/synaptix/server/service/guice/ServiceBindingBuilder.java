package com.synaptix.server.service.guice;

public interface ServiceBindingBuilder<F> {

	public ServiceBindingBuilder<F> with(Class<? super F> serviceClass);

}
