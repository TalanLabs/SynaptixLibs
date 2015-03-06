package com.synaptix.gwt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SynaptixGWTComponent {

	public boolean createBuilder() default true;

	public boolean createFields() default true;

	public boolean createImpl() default true;

}
