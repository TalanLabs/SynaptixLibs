package com.synaptix.entity.extension;

import java.io.Serializable;
import java.util.Date;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import com.synaptix.entity.IId;

public enum JdbcTypesEnum {

	DOUBLE {
		@Override
		public boolean matches(Class<?> c, DatabaseLanguage databaseLanguage) {
			if (Double.class.isAssignableFrom(c)) {
				return true;
			}
			return false;
		}
	},
	REAL {
		@Override
		public boolean matches(Class<?> c, DatabaseLanguage databaseLanguage) {
			if (Float.class.isAssignableFrom(c)) {
				return true;
			}
			return false;
		}
	},
	INTEGER {
		@Override
		public boolean matches(Class<?> c, DatabaseLanguage databaseLanguage) {
			if (Integer.class.isAssignableFrom(c)) {
				return true;
			}
			return false;
		}
	},
	NUMERIC {
		@Override
		public boolean matches(Class<?> c, DatabaseLanguage databaseLanguage) {
			if (Number.class.isAssignableFrom(c)) {
				return true;
			}
			return false;
		}
	},
	DATE {
		@Override
		public boolean matches(Class<?> c, DatabaseLanguage databaseLanguage) {
			if (Date.class.isAssignableFrom(c) || LocalDateTime.class.isAssignableFrom(c) || LocalTime.class.isAssignableFrom(c) || LocalDate.class.isAssignableFrom(c)) {
				return true;
			}
			return false;
		}
	},
	TIMESTAMP {
		@Override
		public boolean matches(Class<?> c, DatabaseLanguage databaseLanguage) {
			if (Date.class.isAssignableFrom(c)) {
				return true;
			}
			return false;
		}
	},
	CHAR {
		@Override
		public boolean matches(Class<?> c, DatabaseLanguage databaseLanguage) {
			if (databaseLanguage == DatabaseLanguage.ORACLE && (Boolean.class.isAssignableFrom(c) || boolean.class.isAssignableFrom(c))) {
				return true;
			}
			return false;
		}
	},
	BOOLEAN {
		@Override
		public boolean matches(Class<?> c, DatabaseLanguage databaseLanguage) {
			if (databaseLanguage == DatabaseLanguage.POSTGRESQL && (Boolean.class.isAssignableFrom(c) || boolean.class.isAssignableFrom(c))) {
				return true;
			}
			return false;
		}
	},
	BLOB {
		@Override
		public boolean matches(Class<?> c, DatabaseLanguage databaseLanguage) {
			if (c.isArray() && c.getComponentType().equals(Byte.TYPE)) {
				return true;
			}
			return false;

		}
	},
	VARCHAR {
		@Override
		public boolean matches(Class<?> c, DatabaseLanguage databaseLanguage) {
			if (String.class.isAssignableFrom(c) || Enum.class.isAssignableFrom(c)) {
				return true;
			}
			if (databaseLanguage == DatabaseLanguage.ORACLE && (IId.class.isAssignableFrom(c) || Serializable.class.isAssignableFrom(c))) {
				return true;
			}
			return false;
		}
	},
	OTHER {
		@Override
		public boolean matches(Class<?> c, DatabaseLanguage databaseLanguage) {
			if (databaseLanguage == DatabaseLanguage.POSTGRESQL && (IId.class.isAssignableFrom(c) || Serializable.class.isAssignableFrom(c))) {
				return true;
			}
			return false;
		}
	},
	NONE {
		@Override
		public boolean matches(Class<?> c, DatabaseLanguage databaseLanguage) {
			return true;
		}
	};

	/**
	 * Determine which JDBC type a Java type corresponds to.
	 */
	public static JdbcTypesEnum which(Class<?> c, DatabaseLanguage databaseLanguage) {
		for (JdbcTypesEnum type : JdbcTypesEnum.values()) {
			if (type.matches(c, databaseLanguage)) {
				return type;
			}
		}
		throw new RuntimeException("Java type does not match to JDBC type");
	}

	/**
	 * Returns {@code true} if the JdbcTypeEnum matches the class.
	 */
	public abstract boolean matches(Class<?> c, DatabaseLanguage databaseLanguage);
}
