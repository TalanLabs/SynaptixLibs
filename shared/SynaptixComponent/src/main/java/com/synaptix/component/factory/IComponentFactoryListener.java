package com.synaptix.component.factory;

import com.synaptix.component.IComponent;

public interface IComponentFactoryListener {

	/**
	 * Call after create instance of interfaceClass
	 * 
	 * @param interfaceClass
	 * @param instance
	 */
	public <G extends IComponent> void afterCreated(Class<G> interfaceClass, G instance);

}
