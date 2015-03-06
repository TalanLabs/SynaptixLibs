package com.synaptix.redpepper.web.annotations.gwt.shared;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AutoField {
	public String debugId() default "";

	public AutoWebType uiType() default AutoWebType.none;
}
