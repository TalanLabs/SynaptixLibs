package com.synaptix.mybatis.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.synaptix.mybatis.component.mapper.ComponentMapperManager;
import com.synaptix.mybatis.component.mapper.IComponentMapper;

public class SynaptixComponentMapperProvider<E extends IComponentMapper> implements Provider<E> {

	private final Class<E> componentMapperClass;

	private ComponentMapperManager componentMapperManager;

	public SynaptixComponentMapperProvider(Class<E> componentMapperClass) {
		super();
		this.componentMapperClass = componentMapperClass;
	}

	@Inject
	public void setComponentMapperManager(ComponentMapperManager componentMapperManager) {
		this.componentMapperManager = componentMapperManager;

		componentMapperManager.addComponentMapper(componentMapperClass);
	}

	@Override
	public E get() {
		return componentMapperManager.getComponentMapper(componentMapperClass);
	}
}
