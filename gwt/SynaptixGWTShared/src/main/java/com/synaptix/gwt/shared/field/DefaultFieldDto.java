package com.synaptix.gwt.shared.field;

public class DefaultFieldDto implements IFieldDto {

	private final String name;

	public DefaultFieldDto(String name) {
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
