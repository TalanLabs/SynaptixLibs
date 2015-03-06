package com.synaptix.swing.event;

import java.util.EventObject;

public class PlanningDataEvent extends EventObject {

	private static final long serialVersionUID = -1863955321922697371L;

	public enum Type { CONTENTS_CHANGED }
	
	private Type type;
	
	public PlanningDataEvent(Object source,Type type) {
		super(source);
		this.type = type;
	}

	public Type getType() {
		return type;
	}
}
