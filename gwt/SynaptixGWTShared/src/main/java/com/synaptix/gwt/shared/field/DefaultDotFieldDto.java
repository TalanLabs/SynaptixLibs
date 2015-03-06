package com.synaptix.gwt.shared.field;

public class DefaultDotFieldDto<E> extends DefaultFieldDto implements IDotFieldDto<E> {

	private final E sub;

	public DefaultDotFieldDto(E sub, String name) {
		super(name);

		this.sub = sub;
	}

	@Override
	public E dot() {
		return sub;
	}
}