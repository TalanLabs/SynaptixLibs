package com.synaptix.entity.extension;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.synaptix.component.IComponent;
import com.synaptix.component.extension.IPropertyExtensionDescriptor;

public class DatabasePropertyExtensionDescriptor implements IPropertyExtensionDescriptor {

	private final Column column;

	private final Collection collection;

	private final NlsColumn nlsColumn;

	public DatabasePropertyExtensionDescriptor(Column column, Collection collection, NlsColumn nlsMeaning) {
		super();
		this.column = column;
		this.collection = collection;
		this.nlsColumn = nlsMeaning;
	}

	public Column getColumn() {
		return column;
	}

	public Collection getCollection() {
		return collection;
	}

	public NlsColumn getNlsColumn() {
		return nlsColumn;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public static class Column {

		private final String sqlName;

		private final int sqlSize;

		private final boolean notNull;

		private final String defaultValue;

		private final JdbcTypesEnum jdbcType;

		private final boolean readOnly;

		private final boolean upperOnly;

		public Column(String sqlName, int sqlSize, boolean notNull, String defaultValue, JdbcTypesEnum jdbcType, boolean readOnly, boolean upperOnly) {
			super();
			this.sqlName = sqlName;
			this.sqlSize = sqlSize;
			this.notNull = notNull;
			this.defaultValue = defaultValue;
			this.jdbcType = jdbcType;
			this.readOnly = readOnly;
			this.upperOnly = upperOnly;
		}

		public String getSqlName() {
			return sqlName;
		}

		public int getSqlSize() {
			return sqlSize;
		}

		public boolean isNotNull() {
			return notNull;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public JdbcTypesEnum getJdbcType() {
			return jdbcType;
		}

		public boolean isReadOnly() {
			return readOnly;
		}

		public boolean isUpperOnly() {
			return upperOnly;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	public static class Collection {

		private final String sqlTableName;

		private final String schema;

		private final String idSource;

		private final String idTarget;

		private final String alias;

		private final Class<? extends IComponent> componentClass;

		public Collection(String sqlTableName, String schema, String idSource, String idTarget, String alias, Class<? extends IComponent> componentClass) {
			super();
			this.sqlTableName = sqlTableName;
			this.schema = schema;
			this.idSource = idSource;
			this.idTarget = idTarget;
			this.alias = alias;
			this.componentClass = componentClass;
		}

		public String getSqlTableName() {
			return sqlTableName;
		}

		public String getSchema() {
			return schema;
		}

		public String getIdSource() {
			return idSource;
		}

		public String getIdTarget() {
			return idTarget;
		}

		public String getAlias() {
			return alias;
		}

		public Class<? extends IComponent> getComponentClass() {
			return componentClass;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	public static class NlsColumn {

		private final String sqlName;

		private final int sqlSize;

		private final boolean notNull;

		private final boolean upperOnly;

		public NlsColumn(String sqlName, int sqlSize, boolean notNull, boolean upperOnly) {
			super();
			this.sqlName = sqlName;
			this.sqlSize = sqlSize;
			this.notNull = notNull;
			this.upperOnly = upperOnly;
		}

		public String getSqlName() {
			return sqlName;
		}

		public int getSqlSize() {
			return sqlSize;
		}

		public boolean isNotNull() {
			return notNull;
		}

		public boolean isUpperOnly() {
			return upperOnly;
		}
	}
}
