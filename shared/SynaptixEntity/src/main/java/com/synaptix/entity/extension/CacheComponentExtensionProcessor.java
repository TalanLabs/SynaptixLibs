package com.synaptix.entity.extension;

import com.synaptix.component.IComponent;
import com.synaptix.component.extension.IClassExtensionDescriptor;
import com.synaptix.component.factory.AbstractComponentExtensionProcessor;
import com.synaptix.entity.extension.ICacheComponentExtension.Cache;

public class CacheComponentExtensionProcessor extends AbstractComponentExtensionProcessor {

	@Override
	public IClassExtensionDescriptor createClassExtensionDescriptor(Class<? extends IComponent> componentClass) {
		IClassExtensionDescriptor classExtensionDescriptor = null;
		Cache cache = componentClass.getAnnotation(Cache.class);
		if (cache != null) {
			classExtensionDescriptor = new CacheClassExtensionDescriptor(cache.readOnly(), cache.size(), cache.clearInterval(), cache.links());
		}
		return classExtensionDescriptor;
	}
}
