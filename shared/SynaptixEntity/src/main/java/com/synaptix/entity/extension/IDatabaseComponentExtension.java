package com.synaptix.entity.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.synaptix.component.IComponent;

public interface IDatabaseComponentExtension {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface UpperOnly {

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface JdbcType {

		public JdbcTypesEnum value() default JdbcTypesEnum.NONE;

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface DefaultValue {

		public String value();

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	/**
	 * 
	 * @author Nicolas P
	 *
	 */
	@interface Collection {

		public String sqlTableName();

		public String schema() default "";

		/**
		 * The id of the entity in the current table name (the entity which uses the collection annotation)
		 * 
		 * @return
		 */
		public String idSource();

		/**
		 * The id of the entity in the sql table name
		 * 
		 * @return
		 */
		public String idTarget() default "ID";

		/**
		 * Alias for nested resultMaps (MyBatis)
		 * 
		 * @return
		 */
		public String alias() default "";

		public Class<? extends IComponent> componentClass();

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface NlsColumn {

	}
}
