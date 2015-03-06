package com.synaptix.swing.event;

import java.util.EventObject;

import com.synaptix.swing.search.SearchModel;

public class SearchModelEvent extends EventObject {

	private static final long serialVersionUID = 3021687478113714244L;

	public enum Type { FILTER }
	
	private Type type;
	
	public SearchModelEvent(SearchModel source,Type type) {
		super(source);
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
}
