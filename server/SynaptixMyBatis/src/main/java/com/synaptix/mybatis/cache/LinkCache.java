package com.synaptix.mybatis.cache;

import com.synaptix.component.IComponent;

public class LinkCache {

	private final Class<? extends IComponent> parentClass;

	private final Class<? extends IComponent> linkClass;

	public LinkCache(Class<? extends IComponent> parentClass, Class<? extends IComponent> linkClass) {
		super();
		this.parentClass = parentClass;
		this.linkClass = linkClass;
	}

	public Class<? extends IComponent> getParentClass() {
		return parentClass;
	}

	public Class<? extends IComponent> getLinkClass() {
		return linkClass;
	}
}
