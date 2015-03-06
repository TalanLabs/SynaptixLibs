package com.synaptix.widget.view.swing.descriptor;

import java.util.List;
import java.util.Set;

import com.synaptix.component.IComponent;
import com.synaptix.swing.search.Filter;
import com.synaptix.widget.component.view.swing.DefaultSearchTablePageComponentsPanel;
import com.synaptix.widget.perimeter.view.swing.IPerimeterWidget;
import com.synaptix.widget.perimeter.view.swing.IPerimeterWidgetDescriptor;
import com.synaptix.widget.perimeter.view.swing.PerimeterWidgetDescriptorBuilder;
import com.synaptix.widget.perimeter.view.swing.descriptor.IPerimeterDescriptor;

/**
 * A panel descriptor used by DefaultSearchComponentsPanel
 * 
 * @author Nicolas P
 * 
 */
public abstract class AbstractSearchViewDescriptor<E extends IComponent> extends AbstractCommonViewDescriptor<E> {

	public abstract String[] getFilterColumns();

	/**
	 * First call
	 * 
	 * @param defaultSearchComponentsPanel
	 */
	public abstract void install(DefaultSearchTablePageComponentsPanel<E> defaultSearchComponentsPanel);

	/**
	 * Specific filters
	 * 
	 * @return
	 */
	public abstract List<Filter> getSpecificFilters();

	/**
	 * Specific perimeters
	 * 
	 * @return
	 */
	public abstract List<IPerimeterWidgetDescriptor> getSpecificPerimeters();

	/**
	 * Build a perimeter widget descriptor with given id and perimeter widget<br/>
	 * 
	 * @param id
	 * @param perimeterWidget
	 * @return
	 */
	protected final IPerimeterWidgetDescriptor buildPerimeterWidgetDescriptor(String id, IPerimeterWidget perimeterWidget) {
		return new PerimeterWidgetDescriptorBuilder().id(id).perimeterWidget(perimeterWidget).build();
	}

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
	 * Start search when opening panel
	 * 
	 * @return
	 */
	public boolean isSearchAtOpening() {
		return true;
	}

	/**
	 * Is line number editable?
	 * 
	 * @return
	 */
	public boolean isLineEditable() {
		return false;
	}

	/**
	 * Use this to switch to perimeters filters.<br/>
	 * Null means use "Default mode", which is the filters in horizontal.
	 * 
	 * @return
	 */
	public IPerimeterDescriptor createPerimeter() {
		return null;
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
