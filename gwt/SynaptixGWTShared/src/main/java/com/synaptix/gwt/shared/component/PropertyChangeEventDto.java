package com.synaptix.gwt.shared.component;

public class PropertyChangeEventDto extends java.util.EventObject {

	private static final long serialVersionUID = -414932478519087825L;

	/**
	 * Constructs a new <code>PropertyChangeEvent</code>.
	 * 
	 * @param source
	 *            The bean that fired the event.
	 * @param propertyName
	 *            The programmatic name of the property that was changed.
	 * @param oldValue
	 *            The old value of the property.
	 * @param newValue
	 *            The new value of the property.
	 */
	public PropertyChangeEventDto(Object source, String propertyName, Object oldValue, Object newValue) {
		super(source);
		this.propertyName = propertyName;
		this.newValue = newValue;
		this.oldValue = oldValue;
	}

	/**
	 * Gets the programmatic name of the property that was changed.
	 * 
	 * @return The programmatic name of the property that was changed. May be
	 *         null if multiple properties have changed.
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * Gets the new value for the property, expressed as an Object.
	 * 
	 * @return The new value for the property, expressed as an Object. May be
	 *         null if multiple properties have changed.
	 */
	public Object getNewValue() {
		return newValue;
	}

	/**
	 * Gets the old value for the property, expressed as an Object.
	 * 
	 * @return The old value for the property, expressed as an Object. May be
	 *         null if multiple properties have changed.
	 */
	public Object getOldValue() {
		return oldValue;
	}

	/**
	 * Sets the propagationId object for the event.
	 * 
	 * @param propagationId
	 *            The propagationId object for the event.
	 */
	public void setPropagationId(Object propagationId) {
		this.propagationId = propagationId;
	}

	/**
	 * The "propagationId" field is reserved for future use. In Beans 1.0 the
	 * sole requirement is that if a listener catches a PropertyChangeEvent and
	 * then fires a PropertyChangeEvent of its own, then it should make sure
	 * that it propagates the propagationId field from its incoming event to its
	 * outgoing event.
	 * 
	 * @return the propagationId object associated with a bound/constrained
	 *         property update.
	 */
	public Object getPropagationId() {
		return propagationId;
	}

	/**
	 * name of the property that changed. May be null, if not known.
	 * 
	 * @serial
	 */
	private String propertyName;

	/**
	 * New value for property. May be null if not known.
	 * 
	 * @serial
	 */
	private Object newValue;

	/**
	 * Previous value for property. May be null if not known.
	 * 
	 * @serial
	 */
	private Object oldValue;

	/**
	 * Propagation ID. May be null.
	 * 
	 * @serial
	 * @see #getPropagationId.
	 */
	private Object propagationId;

}
