package com.synaptix.client.observable;

public interface ObserverListener<E> {

	public abstract void changedValue(Observable<E> observable, E e);

}
