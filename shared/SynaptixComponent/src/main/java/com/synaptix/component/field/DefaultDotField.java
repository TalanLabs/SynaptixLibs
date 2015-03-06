package com.synaptix.component.field;

public class DefaultDotField<E> extends DefaultField implements IDotField<E> {

	private final E sub;

	public DefaultDotField(E sub, String name) {
		super(name);

		this.sub = sub;
	}

	@Override
	public E dot() {
		return sub;
	}
}