package com.synaptix.service.filter.leaf;

import org.apache.commons.lang.builder.ToStringBuilder;

public abstract class AbstractPropertyValue<E> extends AbstractProperty {

	private static final long serialVersionUID = 1734921191585202202L;

	private final E value;

	public AbstractPropertyValue(String propertyName, E value) {
		super(propertyName);

		assert value != null;

		this.value = value;
	}

	public final E getValue() {
		return value;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
