package com.synaptix.service.filter.leaf;

import org.apache.commons.lang.builder.ToStringBuilder;

public class AssossiationPropertyValue<E> extends AbstractPropertyValue<E> {

	private static final long serialVersionUID = -5610951471705700570L;

	public enum Type {
		exists, not_exists
	}

	private final Type type;

	private final String assossiationSqlTable;

	private final String assossiationSqlColumn;

	public AssossiationPropertyValue(Type type, String propertyName, String assossiationSqlTable, String assossiationSqlColumn, E value) {
		super(propertyName, value);

		this.type = type;
		this.assossiationSqlTable = assossiationSqlTable;
		this.assossiationSqlColumn = assossiationSqlColumn;
	}

	public Type getType() {
		return type;
	}

	public String getAssossiationSqlTable() {
		return assossiationSqlTable;
	}

	public String getAssossiationSqlColumn() {
		return assossiationSqlColumn;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
