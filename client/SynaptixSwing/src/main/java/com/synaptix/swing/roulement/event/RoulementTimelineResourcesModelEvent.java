package com.synaptix.swing.roulement.event;

import java.util.EventObject;

public class RoulementTimelineResourcesModelEvent extends EventObject {

	private static final long serialVersionUID = -5486713666232753728L;

	protected int fromIndex;

	protected int toIndex;

	public RoulementTimelineResourcesModelEvent(Object source, int from,
			int to) {
		super(source);
		fromIndex = from;
		toIndex = to;
	}

	public int getFromIndex() {
		return fromIndex;
	};

	public int getToIndex() {
		return toIndex;
	};
}
