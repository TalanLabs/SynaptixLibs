package com.synaptix.swing.event;

import java.util.EventObject;

public class SimpleDaysTimelineResourcesModelEvent extends EventObject {

	private static final long serialVersionUID = -5486713666232753728L;

	protected int fromIndex;

	protected int toIndex;

	public SimpleDaysTimelineResourcesModelEvent(Object source, int from,
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
