package com.synaptix.entity.extension;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.synaptix.component.extension.IClassExtensionDescriptor;

public class DatabaseClassExtensionDescriptor implements IClassExtensionDescriptor {

	private final String schema;

	private final String sqlTableName;

	public DatabaseClassExtensionDescriptor(String schema, String sqlTableName) {
		super();
		this.sqlTableName = sqlTableName;
		this.schema = schema;
	}

	public String getSchema() {
		return schema;
	}

	public String getSqlTableName() {
		return sqlTableName;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
