package com.synaptix.component;

import java.beans.PropertyChangeListener;

/**
 * IPropertyChangeCapable : Describe a add and remove listen on a property
 * 
 * @author Gaby
 * 
 */
public interface IPropertyChangeCapable {

	/**
	 * Add a listener on all properties value change
	 * 
	 * @param l
	 *            a listener property change
	 */
	public void addPropertyChangeListener(PropertyChangeListener l);

	/**
	 * Remove a listner on all properties value change
	 * 
	 * @param l
	 *            a listener property change
	 */
	public void removePropertyChangeListener(PropertyChangeListener l);

	/**
	 * Add a listener on property value change
	 * 
	 * @param propertyName
	 *            a property name
	 * @param l
	 *            a listener property change
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener l);

	/**
	 * Remove a listner on property value change
	 * 
	 * @param propertyName
	 *            a property name
	 * @param l
	 *            a listener property change
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener l);

}
