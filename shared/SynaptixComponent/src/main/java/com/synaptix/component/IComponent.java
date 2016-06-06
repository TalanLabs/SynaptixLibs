package com.synaptix.component;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.Set;

/**
 * IComponent
 * 
 * Create a instance of IComponent, call <link>ComponentFactory</link>
 * 
 * @author Gaby
 * 
 */
public interface IComponent extends IPropertyChangeCapable, Serializable {

	/**
	 * Field is use for equals and hascode
	 * 
	 * @author Gaby
	 * 
	 */
	@Target({ ElementType.METHOD })
	@Retention(RetentionPolicy.RUNTIME)
	@interface EqualsKey {
		boolean nullEquals() default true;
	}

	/**
	 * Field is computed. Value is class implementation
	 * 
	 * @author Gaby
	 * 
	 */
	@Target({ ElementType.METHOD })
	@Retention(RetentionPolicy.RUNTIME)
	@interface Computed {

		public Class<?> value();
	}

	/**
	 * Get a map properties value in Component
	 * 
	 * @return Current properties values.
	 */
	public Map<String, Object> straightGetProperties();

	/**
	 * Get a property value in Component
	 * 
	 * @param propertyName
	 *            the name of the property to get.
	 * @return the current value of the property.
	 */
	public Object straightGetProperty(String propertyName);

	/**
	 * Update a property value in Component and fire a <code>PropertyChangeEvent</code>.
	 * 
	 * @param properties
	 *            the properties to set.
	 */
	public void straightSetProperties(Map<String, Object> properties);

	/**
	 * Update a map properties value in Component and fire a <code>PropertyChangeEvent</code>.
	 * 
	 * @param propertyName
	 *            the name of the property to set.
	 * @param value
	 *            the value to set the property with.
	 */
	public void straightSetProperty(String propertyName, Object value);

	/**
	 * Get a list of property name
	 * 
	 * @return the list of property name
	 */
	public Set<String> straightGetPropertyNames();

	/**
	 * Get a class of property name
	 * 
	 * @param propertyName
	 *            the name of the property to get class
	 * @return the class of the property
	 */
	public Class<?> straightGetPropertyClass(String propertyName);
}
