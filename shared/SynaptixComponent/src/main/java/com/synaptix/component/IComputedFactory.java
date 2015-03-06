package com.synaptix.component;

public interface IComputedFactory {

	public <E> E createInstance(Class<E> clazz);

}
