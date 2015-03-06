package com.synaptix.widget.view.swing.descriptor;

import com.synaptix.component.IComponent;
import com.synaptix.widget.component.view.swing.DefaultTableComponentsPanel;

public abstract class AbstractTableViewDescriptor<E extends IComponent> extends AbstractCommonViewDescriptor<E> {

	/**
	 * Fisrt call
	 * 
	 * @param defaultTableComponentsPanel
	 */
	public abstract void install(DefaultTableComponentsPanel<E> defaultTableComponentsPanel);

}
