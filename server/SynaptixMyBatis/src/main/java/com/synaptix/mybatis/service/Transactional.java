package com.synaptix.mybatis.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Transactional {

	/**
	 * Commit if ok
	 * 
	 * @return
	 */
	boolean commit() default false;

}
