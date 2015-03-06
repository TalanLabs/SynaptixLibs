package com.synaptix.widget.view.swing.descriptor;

import java.util.Set;

import com.synaptix.component.IComponent;
import com.synaptix.widget.component.view.swing.DefaultTablePageComponentsPanel;

/**
 * A panel descriptor used by DefaultSearchComponentsPanel
 * 
 * @author Nicolas P
 * 
 */
public abstract class AbstractTablePageViewDescriptor<E extends IComponent> extends AbstractCommonViewDescriptor<E> {

	/**
	 * First call
	 * 
	 * @param defaultTablePageComponentsPanel
	 */
	public abstract void install(DefaultTablePageComponentsPanel<E> defaultTablePageComponentsPanel);

	/**
	 * Get the column list
	 * 
	 * @return
	 */
	public abstract Set<String> getColumns();

	/**
	 * Use all columns for the pagination<br/>
	 * If false, uses only visible columns
	 */
	public abstract boolean useAllColumns();

	/**
	 * Is line number editable?
	 * 
	 * @return
	 */
	public boolean isLineEditable() {
		return false;
	}

	/**
	 * When changing components, should the table keep the selected components if still here?
	 * 
	 * @return
	 */
	public boolean keepSelectionOnChange() {
		return false;
	}
}
