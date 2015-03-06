package com.synaptix.client.view.header;

import java.util.EventObject;

public class HeaderEvent extends EventObject {

	private static final long serialVersionUID = -4288191224052995999L;

	public enum Type {
		HEADER_CHANGED
	}

	private Type type;

	public HeaderEvent(IHeader source, Type type) {
		super(source);

		this.type = type;
	}

	public Type getType() {
		return type;
	}

}
