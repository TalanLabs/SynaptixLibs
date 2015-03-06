package com.synaptix.entity.extension;

import java.lang.reflect.Method;

import com.synaptix.component.IComponent;
import com.synaptix.component.extension.IPropertyExtensionDescriptor;
import com.synaptix.component.factory.AbstractComponentExtensionProcessor;
import com.synaptix.entity.extension.IBusinessComponentExtension.BusinessKey;

public class BusinessComponentExtensionProcessor extends AbstractComponentExtensionProcessor {

	@Override
	public IPropertyExtensionDescriptor createPropertyExtensionDescriptor(Class<? extends IComponent> componentClass, Method getterMethod) {
		IPropertyExtensionDescriptor propertyExtensionDescriptor = null;
		BusinessKey businessKey = getterMethod.getAnnotation(BusinessKey.class);
		if (businessKey != null) {
			propertyExtensionDescriptor = new BusinessPropertyExtensionDescriptor(businessKey.order());
		}
		return propertyExtensionDescriptor;
	}

}
