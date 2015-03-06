package com.synaptix.pmgr.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to define a flux<br>
 * fluxCode: name of the flux<br>
 * extension: the extension of the flux (used for export flux)
 * 
 * @author Nicolas P
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Flux {

	public String fluxCode();

	/**
	 * Extension used for export only. To be fixed?
	 * 
	 * @return
	 */
	public String extension() default "";

}
