package com.synaptix.component.factory;

import java.lang.reflect.Method;

import com.synaptix.component.IComponent;
import com.synaptix.component.extension.IClassExtensionDescriptor;
import com.synaptix.component.extension.IComponentExtensionProcessor;
import com.synaptix.component.extension.IPropertyExtensionDescriptor;

public abstract class AbstractComponentExtensionProcessor implements IComponentExtensionProcessor {

	@Override
	public IClassExtensionDescriptor createClassExtensionDescriptor(Class<? extends IComponent> componentClass) {
		return null;
	}

	@Override
	public IPropertyExtensionDescriptor createPropertyExtensionDescriptor(Class<? extends IComponent> componentClass, Method getterMethod) {
		return null;
	}
}
