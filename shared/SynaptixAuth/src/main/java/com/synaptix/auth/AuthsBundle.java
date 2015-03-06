package com.synaptix.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Gaby
 * 
 */
public interface AuthsBundle {

	/**
	 * The key is mandatory. It defines the action and purpose of each
	 * authorization.
	 * 
	 * @author Gaby
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface Key {

		String object();

		String action();

	}

	/**
	 * Descripte a authorization in humain language
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
