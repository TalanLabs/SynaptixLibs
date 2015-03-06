package com.synaptix.widget.view.swing.tablemodel;

import com.synaptix.component.IComponent;
import com.synaptix.swing.table.Column;

public interface IColumnTableFactory<E extends IComponent> {

	/**
	 * Create a column for propertyName
	 * 
	 * @param propertyName
	 * @return
	 */
	public Column createColumn(String propertyName);

	/**
	 * Get a value for propertyName in component
	 * 
	 * @param propertyName
	 * @param component
	 * @return
	 */
	public Object getValue(String propertyName, E component);

	/**
	 * Set a value for propertyName in component
	 * 
	 * @param propertyName
	 * @param component
	 * @param value
	 */
	public void setValue(String propertyName, E component, Object value);

}
