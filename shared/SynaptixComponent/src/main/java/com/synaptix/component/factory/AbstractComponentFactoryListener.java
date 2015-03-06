package com.synaptix.component.factory;

import com.synaptix.component.IComponent;

public abstract class AbstractComponentFactoryListener implements IComponentFactoryListener {

	@Override
	public <G extends IComponent> void afterCreated(Class<G> interfaceClass, G instance) {
	}
}
