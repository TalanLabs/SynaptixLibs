package com.synaptix.client.observable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractObservable<E> implements Observable<E> {

	private E value;

	protected List<ObserverListener<E>> listenerList;

	private boolean enableListeners = true;

	public AbstractObservable() {
		value = null;
		listenerList = new ArrayList<ObserverListener<E>>();
	}

	@Override
	public void addObserverListener(ObserverListener<E> l) {
		listenerList.add(l);
	}

	@Override
	public void removeObserverListener(ObserverListener<E> l) {
		listenerList.remove(l);
	}

	@Override
	public E getValue() {
		return value;
	}

	@Override
	public void setValue(E e) {
		this.value = e;

		fireChangedValues(e);
	}

	protected void fireChangedValues(E e) {
		if (enableListeners) {
			for (int i = listenerList.size() - 1; i >= 0; i--) {
				ObserverListener<E> l = listenerList.get(i);
				l.changedValue(this, e);
			}
		}
	}

	public void setEnableListeners(boolean enableListeners) {
		this.enableListeners = enableListeners;
	}
}
