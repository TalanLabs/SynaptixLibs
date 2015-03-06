package com.synaptix.swing.list;

import javax.swing.event.ChangeListener;

public interface Filters<E> {

	public abstract boolean isFilter(E value);

	public abstract void addChangeListener(ChangeListener cl);

	public abstract void removeChangeListener(ChangeListener cl);

}
