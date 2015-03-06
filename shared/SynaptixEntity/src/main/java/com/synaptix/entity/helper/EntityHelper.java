package com.synaptix.entity.helper;

import java.util.HashSet;
import java.util.Set;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentDescriptor;
import com.synaptix.component.factory.ComponentDescriptor.PropertyDescriptor;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.entity.INlsMessage;
import com.synaptix.entity.extension.DatabasePropertyExtensionDescriptor;
import com.synaptix.entity.extension.IDatabaseComponentExtension;

public class EntityHelper {

	/**
	 * Find chidren for component class, all level
	 * 
	 * @param componentClass
	 * @return
	 */
	public static final <E extends IComponent> Set<Class<? extends IComponent>> findAllChildren(Class<E> componentClass) {
		Set<Class<? extends IComponent>> res = new HashSet<Class<? extends IComponent>>();
		findAllChildren(res, componentClass);
		return res;
	}

	private static final <E extends IComponent> void findAllChildren(Set<Class<? extends IComponent>> res, Class<E> componentClass) {
		Set<Class<? extends IComponent>> childs = findChildren(componentClass);
		if (childs != null && !childs.isEmpty()) {
			for (Class<? extends IComponent> child : childs) {
				if (!res.contains(child)) {
					res.add(child);
					findAllChildren(res, child);
				}
			}
		}
	}

	/**
	 * Find children for component class, 1 level
	 * 
	 * @param componentClass
	 * @return
	 */
	public static final <E extends IComponent> Set<Class<? extends IComponent>> findChildren(Class<E> componentClass) {
		Set<Class<? extends IComponent>> res = new HashSet<Class<? extends IComponent>>();
		ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(componentClass);
		for (String propertyName : cd.getPropertyNames()) {
			PropertyDescriptor pd = cd.getPropertyDescriptor(propertyName);
			DatabasePropertyExtensionDescriptor dp = (DatabasePropertyExtensionDescriptor) pd.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);

			if (dp != null) {
				if (dp.getCollection() != null && dp.getCollection().getComponentClass() != null) {
					res.add(dp.getCollection().getComponentClass());
				}
			}
		}
		return res;
	}

	/**
	 * Return if component use nls message
	 * 
	 * @param componentClass
	 * @return
	 */
	public static final <E extends IComponent> boolean useNlsMessage(Class<E> componentClass) {
		if (INlsMessage.class.isAssignableFrom(componentClass)) {
			return true;
		}
		ComponentDescriptor cd = ComponentFactory.getInstance().getDescriptor(componentClass);
		for (String propertyName : cd.getPropertyNames()) {
			PropertyDescriptor pd = cd.getPropertyDescriptor(propertyName);
			DatabasePropertyExtensionDescriptor dp = (DatabasePropertyExtensionDescriptor) pd.getPropertyExtensionDescriptor(IDatabaseComponentExtension.class);

			if (dp != null) {
				if (dp.getCollection() != null && dp.getCollection().getComponentClass() != null) {
					if (useNlsMessage(dp.getCollection().getComponentClass())) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
