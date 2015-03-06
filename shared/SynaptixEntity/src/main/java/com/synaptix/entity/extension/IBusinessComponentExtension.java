package com.synaptix.entity.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface IBusinessComponentExtension {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface BusinessKey {

		public int order() default 0;

	}
}
