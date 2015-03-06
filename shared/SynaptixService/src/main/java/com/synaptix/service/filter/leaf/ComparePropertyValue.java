package com.synaptix.service.filter.leaf;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Compare property value
 * 
 * lower -> COLUMN < value
 * 
 * lower_equal -> COLUMN <= value
 * 
 * equal -> COLUMN = value
 * 
 * upper_equal -> COLUMN >= value
 * 
 * upper -> COLUMN > value
 * 
 * not_equal -> COLUMN <> value
 * 
 * @author Gaby
 * 
 * @param <E>
 */
public class ComparePropertyValue<E> extends AbstractPropertyValue<E> {

	private static final long serialVersionUID = -7551276637666672408L;

	public enum Type {
		lower, lower_equal, equal, upper_equal, upper, not_equal
	}

	private final Type type;

	public ComparePropertyValue(Type type, String propertyName, E value) {
		super(propertyName, value);

		assert type != null;

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
