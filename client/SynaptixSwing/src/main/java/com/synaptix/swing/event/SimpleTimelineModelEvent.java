package com.synaptix.swing.event;

import java.util.EventObject;

public class SimpleTimelineModelEvent extends EventObject {

	private static final long serialVersionUID = -1863955321922697371L;

	public enum Type {
		STRUCTURE_CHANGED, DATA_CHANGED, HEADER_RESOURCE_CHANGED, DATA_RESOURCE_CHANGED
	}

	private Type type;

	private int resource;

	public SimpleTimelineModelEvent(Object source, Type type) {
		this(source, type, -1);
	}

	public SimpleTimelineModelEvent(Object source, Type type, int resource) {
		super(source);
		this.type = type;
		this.resource = resource;
	}

	public Type getType() {
		return type;
	}

	public int getResource() {
		return resource;
	}
}
