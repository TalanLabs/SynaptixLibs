package com.synaptix.client.observable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractObservable<E> implements Observable<E> {

	private E value;

	protected List<ObserverListener<E>> listenerList;

	public AbstractObservable() {
		value = null;
		listenerList = new ArrayList<ObserverListener<E>>();
	}

	public void addObserverListener(ObserverListener<E> l) {
		listenerList.add(l);
	}

	public void removeObserverListener(ObserverListener<E> l) {
		listenerList.remove(l);
	}

	public E getValue() {
		return value;
	}

	public void setValue(E e) {
		this.value = e;

		fireChangedValues(e);
	}

	protected void fireChangedValues(E e) {
		for (int i = listenerList.size() - 1; i >= 0; i--) {
			ObserverListener<E> l = listenerList.get(i);
			l.changedValue(this, e);

		}
	}
}
