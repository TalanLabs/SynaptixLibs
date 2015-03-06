package com.synaptix.service.filter.leaf;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Like property value
 * 
 * like_left_right -> UPPER(COLUMN) like '%' || UPPER(value) || '%'
 * 
 * like_right -> UPPER(COLUMN) like UPPER(value || '%'
 * 
 * like_left -> UPPER(COLUMN) like '%' || UPPER(value)
 * 
 * like -> UPPER(COLUMN) like UPPER(value)
 * 
 * 
 * @author Gaby
 * 
 */
public class LikePropertyValue extends AbstractPropertyValue<String> {

	private static final long serialVersionUID = -5566296343330886967L;

	public enum Type {
		/**
		 * like '%' || UPPER(value)
		 */
		like_left,
		/**
		 * like UPPER(value) || '%'
		 */
		like_right,
		/**
		 * like UPPER(value)
		 */
		like,
		/**
		 * like '%' || UPPER(value) || '%'
		 */
		like_left_right
	}

	private final Type type;

	public LikePropertyValue(Type type, String propertyName, String value) {
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
