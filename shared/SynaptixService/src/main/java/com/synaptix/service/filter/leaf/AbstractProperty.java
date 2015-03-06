package com.synaptix.service.filter.leaf;

import org.apache.commons.lang.builder.ToStringBuilder;

public class AbstractProperty extends AbstractLeaf {

	private static final long serialVersionUID = 5119255911574322672L;

	private final String propertyName;

	public AbstractProperty(String propertyName) {
		super();

		assert propertyName != null;

		this.propertyName = propertyName;
	}

	public final String getPropertyName() {
		return propertyName;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
