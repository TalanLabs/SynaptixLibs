package com.synaptix.constants.shared;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Interface for a localizable resource
 * 
 * @author Gaby
 * 
 */
public interface LocalizableResource {

	/**
	 * Change constant key
	 * 
	 * @author Gaby
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface Key {

		String value();

	}

	/**
	 * Gives the meaning of the constant.
	 * 
	 * Ex : orange => Meaning=Color
	 * 
	 * @author Gaby
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface Meaning {

		String value();

	}

	/**
	 * Descripte a constant in humain language
	 * 
	 * @author Gaby
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface Description {

		String value();

	}
}
