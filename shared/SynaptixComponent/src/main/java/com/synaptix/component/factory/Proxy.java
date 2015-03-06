package com.synaptix.component.factory;

import com.synaptix.component.IComponent;

public interface Proxy {

	public Class<? extends IComponent> straightGetComponentClass();

}
