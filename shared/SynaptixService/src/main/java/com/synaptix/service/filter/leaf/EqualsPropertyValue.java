package com.synaptix.service.filter.leaf;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Equals property value
 * 
 * COLUMN = value
 * 
 * @author Gaby
 * 
 * @param <E>
 */
public class EqualsPropertyValue<E> extends AbstractPropertyValue<E> {

	private static final long serialVersionUID = -5941100711301005197L;

	public EqualsPropertyValue(String propertyName, E value) {
		super(propertyName, value);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
