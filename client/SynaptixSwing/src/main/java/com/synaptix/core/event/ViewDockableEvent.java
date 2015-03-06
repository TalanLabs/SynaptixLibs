package com.synaptix.core.event;

import java.util.EventObject;

import com.synaptix.core.dock.IViewDockable;

public class ViewDockableEvent extends EventObject {

	private static final long serialVersionUID = -4288191224052995999L;

	public enum Type {
		HEADER_CHANGED
	}

	private Type type;

	public ViewDockableEvent(IViewDockable source, Type type) {
		super(source);

		this.type = type;
	}

	public Type getType() {
		return type;
	}
}
