package com.synaptix.swing.event;

import java.util.EventObject;

import com.synaptix.swing.search.SearchFilterModel;

public class SearchFilterModelEvent extends EventObject {

	private static final long serialVersionUID = -1678310995728491041L;

	protected int fromIndex;

	protected int toIndex;

	public SearchFilterModelEvent(SearchFilterModel source, int from, int to) {
		super(source);
		this.fromIndex = from;
		this.toIndex = to;
	}

	public int getFromIndex() {
		return fromIndex;
	}

	public int getToIndex() {
		return toIndex;
	}
}
