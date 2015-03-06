package com.synaptix.swing.event;

import java.util.EventObject;

public class TimelineModelEvent extends EventObject {

	private static final long serialVersionUID = -1863955321922697371L;

	public enum Type { CONTENTS_CHANGED,HEADER_RESSOURCES,MODIFY }
	
	private Type type;
	
	private int ressource;

	public TimelineModelEvent(Object source,Type type) {
		this(source,type,-1);
	}
	
	public TimelineModelEvent(Object source,Type type,int ressource) {
		super(source);
		this.type = type;
		this.ressource = ressource;
	}

	public Type getType() {
		return type;
	}
	
	public int getRessource() {
		return ressource;
	}
}
