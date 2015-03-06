package com.synaptix.swing.event;

import java.util.EventObject;

public class TabbedEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private int index;

	public TabbedEvent(Object source) {
		this(source,-1);
	}

	public TabbedEvent(Object source,int index) {
		super(source);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}
