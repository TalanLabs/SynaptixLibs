package com.synaptix.mybatis.factory;

import java.util.List;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentFactory;

public class ComponentObjectFactory extends DefaultObjectFactory {

	private static final long serialVersionUID = -746902554690684603L;

	@SuppressWarnings("unchecked")
	@Override
	public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
		if (ComponentFactory.isClass(type)) {
			return (T) ComponentFactory.getInstance().createInstance((Class<IComponent>) type);
		}
		return (T) super.create(type, constructorArgTypes, constructorArgs);
	}
}
