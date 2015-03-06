package com.synaptix.widget.grid;

import javax.swing.event.EventListenerList;

public abstract class AbstractValueSelectionModel implements ValueSelectionModel {

	protected EventListenerList listenerList = new EventListenerList();

	@Override
	public void addValueSelectionListener(ValueSelectionListener x) {
		listenerList.add(ValueSelectionListener.class, x);
	}

	@Override
	public void removeValueSelectionListener(ValueSelectionListener x) {
		listenerList.remove(ValueSelectionListener.class, x);
	}

	protected void fireValueChanged(ValueSelectionEvent event) {
		ValueSelectionListener[] ls = listenerList.getListeners(ValueSelectionListener.class);
		for (ValueSelectionListener l : ls) {
			l.valueChanged(event);
		}
	}
}
