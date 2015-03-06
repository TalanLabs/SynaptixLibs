package com.synaptix.gwt.shared.component;

/**
 * IPropertyChangeCapable : Describe a add and remove listen on a property
 * 
 * @author Gaby
 * 
 */
public interface IPropertyChangeCapableDto {

	/**
	 * Add a listener on all properties value change
	 * 
	 * @param l
	 *            a listener property change
	 */
	public void addPropertyChangeListenerDto(PropertyChangeListenerDto l);

	/**
	 * Remove a listner on all properties value change
	 * 
	 * @param l
	 *            a listener property change
	 */
	public void removePropertyChangeListenerDto(PropertyChangeListenerDto l);

	/**
	 * Add a listener on property value change
	 * 
	 * @param propertyName
	 *            a property name
	 * @param l
	 *            a listener property change
	 */
	public void addPropertyChangeListenerDto(String propertyName, PropertyChangeListenerDto l);

	/**
	 * Remove a listner on property value change
	 * 
	 * @param propertyName
	 *            a property name
	 * @param l
	 *            a listener property change
	 */
	public void removePropertyChangeListenerDto(String propertyName, PropertyChangeListenerDto l);

}
