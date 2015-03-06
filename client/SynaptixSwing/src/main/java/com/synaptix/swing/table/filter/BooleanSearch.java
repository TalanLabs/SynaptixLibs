package com.synaptix.swing.table.filter;

import java.io.Serializable;

import com.synaptix.swing.SwingMessages;

public class BooleanSearch implements Serializable {

	private static final long serialVersionUID = 2598971194893477177L;

	public enum Type {
		ALL, TRUE, FALSE
	};

	private Type type;

	public BooleanSearch(Type type) {
		this.type = type;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		String res;
		switch (type) {
		case ALL:
			res = SwingMessages.getString("BooleanSearch.2");
			break;
		case FALSE:
			res = SwingMessages.getString("BooleanSearch.1");
			break;
		case TRUE:
			res = SwingMessages.getString("BooleanSearch.0");
			break;
		default:
			res = SwingMessages.getString("BooleanSearch.3");
			break;
		}
		return res;
	}
}
