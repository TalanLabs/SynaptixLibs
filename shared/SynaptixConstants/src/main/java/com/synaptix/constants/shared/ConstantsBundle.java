package com.synaptix.constants.shared;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Interface for constants bundle
 * 
 * @author Gaby
 * 
 */
public interface ConstantsBundle extends LocalizableResource {

	/**
	 * Define a default string value for a method who return String
	 * 
	 * @author Gaby
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface DefaultStringValue {

		String value();

	}

	/**
	 * Define a default integer value for a method who return Integer or int
	 * 
	 * @author Gaby
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface DefaultIntValue {

		int value();

	}

	/**
	 * Define a default double value for a method who return Double or double
	 * 
	 * @author Gaby
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface DefaultDoubleValue {

		double value();

	}

	/**
	 * Define a default float value for a method who return Float or float
	 * 
	 * @author Gaby
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface DefaultFloatValue {

		float value();

	}

	/**
	 * Define a default boolean value for a method who return Boolean or boolean
	 * 
	 * @author Gaby
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface DefaultBooleanValue {

		boolean value();

	}

	/**
	 * Define a default string array value for a method who return String[]
	 * 
	 * @author Gaby
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface DefaultStringArrayValue {

		String[] value();

	}

	/**
	 * Define a default string map value for a method who return
	 * Map<String,String>
	 * 
	 * @author Gaby
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface DefaultStringMapValue {

		String[] value();

	}
}
