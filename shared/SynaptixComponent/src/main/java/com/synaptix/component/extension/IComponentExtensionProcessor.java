package com.synaptix.component.extension;

import java.lang.reflect.Method;

import com.synaptix.component.IComponent;

public interface IComponentExtensionProcessor {

	/**
	 * Create a class extension descriptor
	 * 
	 * @param componentClass
	 * @return
	 */
	public IClassExtensionDescriptor createClassExtensionDescriptor(Class<? extends IComponent> componentClass);

	/**
	 * Create a property extension descriptor
	 * 
	 * @param getMethod
	 * @return
	 */
	public IPropertyExtensionDescriptor createPropertyExtensionDescriptor(Class<? extends IComponent> componentClass, Method getterMethod);

}
