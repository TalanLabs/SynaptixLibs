package com.synaptix.service.filter.leaf;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Null property
 * 
 * COLUMN is null
 * 
 * COLUMN is not null
 * 
 * @author Gaby
 * 
 * @param <E>
 */
public class NullProperty extends AbstractProperty {

	private static final long serialVersionUID = -5290378237724546340L;

	public enum Type {
		is_null, is_not_null
	}

	private final Type type;

	public NullProperty(Type type, String propertyName) {
		super(propertyName);

		this.type = type;
	}

	public final Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
