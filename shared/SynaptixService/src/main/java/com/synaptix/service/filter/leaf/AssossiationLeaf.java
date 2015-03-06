package com.synaptix.service.filter.leaf;

import org.apache.commons.lang.builder.ToStringBuilder;

public class AssossiationLeaf<E> extends AbstractPropertyValue<E> {

	private static final long serialVersionUID = 5175715070216521150L;

	public enum Type {
		exists, not_exists
	}

	private final Type type;

	private final String assossiationSqlTable;

	private final String assossiationSqlColumn1;

	private final String assossiationSqlColumn2;

	public AssossiationLeaf(Type type, String propertyName, String assossiationSqlTable, String assossiationSqlColumn1, String assossiationSqlColumn2, E value) {
		super(propertyName, value);

		this.type = type;
		this.assossiationSqlTable = assossiationSqlTable;
		this.assossiationSqlColumn1 = assossiationSqlColumn1;
		this.assossiationSqlColumn2 = assossiationSqlColumn2;
	}

	public Type getType() {
		return type;
	}

	public String getAssossiationSqlTable() {
		return assossiationSqlTable;
	}

	public String getAssossiationSqlColumn1() {
		return assossiationSqlColumn1;
	}

	public String getAssossiationSqlColumn2() {
		return assossiationSqlColumn2;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
