package com.synaptix.client.observable;

public interface Observable<E> {

	public void addObserverListener(ObserverListener<E> l);

	public void removeObserverListener(ObserverListener<E> l);

	public void setValue(E value);

	public E getValue();

}
