package com.synaptix.widget.view.swing.descriptor;

import javax.swing.JComponent;

import com.synaptix.component.IComponent;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.swing.JSyTable;
import com.synaptix.widget.view.IViewDescriptor;
import com.synaptix.widget.view.swing.helper.IPopupMenuActionsBuilder;
import com.synaptix.widget.view.swing.helper.IToolBarActionsBuilder;
import com.synaptix.widget.view.swing.tablemodel.IColumnTableFactory;

public abstract class AbstractCommonViewDescriptor<E extends IComponent> implements IViewDescriptor<E> {

	public abstract ConstantsWithLookingBundle getDescriptorBundle();

	public abstract String[] getTableColumns();

	/**
	 * Get table columns is default hide
	 * 
	 * @return
	 */
	public String[] getDefaultHideTableColumns() {
		return null;
	}

	/**
	 * When initcomponent is done
	 */
	public abstract void initialize();

	/**
	 * Install renderers on the given table (enum converter, ...)
	 * 
	 * @param table
	 */
	public abstract void installTable(JSyTable table);

	/**
	 * Install toolBar buttons
	 * 
	 * @return
	 */
	public abstract void installToolBar(IToolBarActionsBuilder builder);

	/**
	 * Install popupMenu
	 * 
	 * @param builder
	 */
	public abstract void installPopupMenu(IPopupMenuActionsBuilder builder);

	/**
	 * Detail component: when a line is selected, displays some information about it under the main table
	 * 
	 * @return
	 */
	public JComponent getDetailComponent() {
		return null;
	}

	/**
	 * Get column table factory, default is null
	 * 
	 * @return
	 */
	public IColumnTableFactory<E> getColumnTableFactory() {
		return null;
	}

	/**
	 * Set column table factory (called by the panel). There is no need to call that from a view descriptor
	 * 
	 * @param columnTableFactory
	 */
	public abstract void setColumnTableFactory(IColumnTableFactory<E> columnTableFactory);
}
