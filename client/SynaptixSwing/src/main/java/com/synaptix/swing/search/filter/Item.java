package com.synaptix.swing.search.filter;

import java.io.Serializable;

public class Item {

	private Serializable key;

	private Object value;

	public Item(Serializable key, Object value) {
		this.key = key;
		this.value = value;
	}

	public Serializable getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}
}
