package com.synaptix.component.field;

public class DefaultField implements IField {

	private final String name;

	public DefaultField(String name) {
		super();
		this.name = name;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
