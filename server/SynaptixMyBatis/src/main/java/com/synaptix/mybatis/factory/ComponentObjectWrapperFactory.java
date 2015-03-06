package com.synaptix.mybatis.factory;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;

import com.synaptix.component.IComponent;

public class ComponentObjectWrapperFactory extends DefaultObjectWrapperFactory {

	@Override
	public boolean hasWrapperFor(Object object) {
		if (object instanceof IComponent) {
			return true;
		}
		return false;
	}

	@Override
	public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
		if (object instanceof IComponent) {
			return new ComponentObjectWrapper(metaObject, (IComponent) object);
		}
		return super.getWrapperFor(metaObject, object);
	}

}
