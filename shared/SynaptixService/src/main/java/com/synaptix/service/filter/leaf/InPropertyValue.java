package com.synaptix.service.filter.leaf;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * In property value
 * 
 * COLUMN in (value1, value2,...)
 * 
 * COLUMN not in (value1, value2,...)
 * 
 * @author Gaby
 * 
 * @param <E>
 */
public class InPropertyValue<E> extends AbstractPropertyValue<E[]> {

	private static final long serialVersionUID = -1141648194406974265L;

	public enum Type {
		in, not_in
	}

	private final Type type;

	public InPropertyValue(Type type, String propertyName, E... values) {
		super(propertyName, values);

		this.type = type;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
