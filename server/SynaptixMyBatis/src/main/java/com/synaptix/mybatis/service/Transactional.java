package com.synaptix.mybatis.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.synaptix.service.exceptions.VersionConflictServiceException;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Transactional {

	/**
	 * Commit if ok
	 */
	boolean commit() default false;

	/**
	 * Checks the version of the entities saved.<br/>
	 * If different, throws a {@link VersionConflictServiceException}<br/>
	 * Default value is true
	 */
	boolean checkVersionConflict() default true;

}
