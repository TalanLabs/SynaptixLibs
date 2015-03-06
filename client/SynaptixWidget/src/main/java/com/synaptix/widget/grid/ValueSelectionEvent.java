package com.synaptix.widget.grid;

import java.util.EventObject;

public class ValueSelectionEvent extends EventObject {

	private static final long serialVersionUID = 3858863495251220968L;

	private final Object[] oldValues;

	private final Object[] newValues;

	private final boolean isAdjusting;

	public ValueSelectionEvent(Object source, Object[] oldValues, Object[] newValues, boolean isAdjusting) {
		super(source);
		this.oldValues = oldValues;
		this.newValues = newValues;
		this.isAdjusting = isAdjusting;
	}

	public Object[] getOldValues() {
		return oldValues;
	}

	public Object[] getNewValues() {
		return newValues;
	}

	public boolean getValueIsAdjusting() {
		return isAdjusting;
	}
}
