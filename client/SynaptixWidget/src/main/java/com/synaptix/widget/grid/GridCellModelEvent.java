package com.synaptix.widget.grid;

import java.util.EventObject;

public class GridCellModelEvent extends EventObject {

	private static final long serialVersionUID = 3858863495251220968L;

	public enum Type {
		ADD, MODIFY, REMOVE
	}

	private final Type type;

	public GridCellModelEvent(Object source, Type type) {
		super(source);
		this.type = type;
	}

	public Type getType() {
		return type;
	}
}
