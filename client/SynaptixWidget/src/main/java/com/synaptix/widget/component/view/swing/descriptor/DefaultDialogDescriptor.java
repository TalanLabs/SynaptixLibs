package com.synaptix.widget.component.view.swing.descriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;

import com.synaptix.component.IComponent;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.search.Filter;
import com.synaptix.swing.search.JSearchHeader;
import com.synaptix.swing.table.AbstractSimpleSpecialTableModel;
import com.synaptix.swing.table.SyTableColumn;
import com.synaptix.widget.component.controller.context.ISearchComponentsContext;
import com.synaptix.widget.component.view.ISearchComponentsDialogViewDescriptor;
import com.synaptix.widget.component.view.swing.DefaultSearchTablePageComponentsPanel;
import com.synaptix.widget.component.view.swing.DefaultTablePageComponentsPanel;
import com.synaptix.widget.perimeter.view.swing.IPerimeterWidgetDescriptor;
import com.synaptix.widget.view.swing.descriptor.AbstractSearchViewDescriptor;
import com.synaptix.widget.view.swing.helper.IPopupMenuActionsBuilder;
import com.synaptix.widget.view.swing.helper.IToolBarActionsBuilder;
import com.synaptix.widget.view.swing.tablemodel.IColumnTableFactory;

/**
 * Abstract panel descriptor with no buttons, no renderers and no specific filters
 *
 * @author Nicolas P
 *
 */
public class DefaultDialogDescriptor<E extends IComponent> extends AbstractSearchViewDescriptor<E> implements ISearchComponentsDialogViewDescriptor<E> {

	protected final ConstantsWithLookingBundle constantsWithLookingBundle;

	protected final String[] filterColumns;

	protected final String[] tableColumns;

	protected final String[] defaultHideTableColumns;

	private ISearchComponentsContext searchComponentsContext;

	private DefaultSearchTablePageComponentsPanel<E> defaultSearchComponentsPanel;

	private boolean detailInitialized;

	private JComponent detailComponent;

	private boolean columnTableFactoryInitialized;

	private IColumnTableFactory<E> columnTableFactory;

	public DefaultDialogDescriptor(ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns, String[] tableColumns) {
		this(constantsWithLookingBundle, filterColumns, tableColumns, null);
	}

	public DefaultDialogDescriptor(ConstantsWithLookingBundle constantsWithLookingBundle, String[] filterColumns, String[] tableColumns, String[] defaultHideTableColumns) {
		super();

		this.constantsWithLookingBundle = constantsWithLookingBundle;
		this.filterColumns = filterColumns;
		this.tableColumns = tableColumns;
		this.defaultHideTableColumns = defaultHideTableColumns;
	}

	@Override
	public final void setSearchComponentsContext(ISearchComponentsContext searchComponentsContext) {
		this.searchComponentsContext = searchComponentsContext;
	}

	@Override
	public void create() {
	}

	protected final ISearchComponentsContext getSearchComponentsContext() {
		return searchComponentsContext;
	}

	@Override
	public ConstantsWithLookingBundle getDescriptorBundle() {
		return constantsWithLookingBundle;
	}

	@Override
	public String[] getFilterColumns() {
		return filterColumns;
	}

	@Override
	public String[] getTableColumns() {
		return tableColumns;
	}

	@Override
	public String[] getDefaultHideTableColumns() {
		return defaultHideTableColumns;
	}

	@Override
	public final Set<String> getColumns() {
		return getTablePageComponentsPanel().getColumns();
	}

	@Override
	public boolean useAllColumns() {
		return true;
	}

	@Override
	public void install(DefaultSearchTablePageComponentsPanel<E> defaultSearchComponentsPanel) {
		this.defaultSearchComponentsPanel = defaultSearchComponentsPanel;
	}

	@Override
	public void initialize() {
	}

	@Override
	public void installTable(JSyTable table) {
	}

	@Override
	public List<Filter> getSpecificFilters() {
		return new ArrayList<Filter>();
	}

	@Override
	public List<IPerimeterWidgetDescriptor> getSpecificPerimeters() {
		return null;
	}

	@Override
	public void installToolBar(IToolBarActionsBuilder builder) {
	}

	@Override
	public void installPopupMenu(IPopupMenuActionsBuilder builder) {
	}

	@Override
	public final JComponent getDetailComponent() {
		if (!detailInitialized) {
			detailComponent = createDetailComponent();
			detailInitialized = true;
		}
		return detailComponent;
	}

	protected JComponent createDetailComponent() {
		return null;
	}

	@Override
	public final IColumnTableFactory<E> getColumnTableFactory() {
		if (!columnTableFactoryInitialized) {
			IColumnTableFactory<E> columnTableFactory = createColumnTableFactory();
			setColumnTableFactory(columnTableFactory);
		}
		return columnTableFactory;
	}

	@Override
	public final void setColumnTableFactory(IColumnTableFactory<E> columnTableFactory) {
		this.columnTableFactory = columnTableFactory;
		columnTableFactoryInitialized = true;
	}

	protected IColumnTableFactory<E> createColumnTableFactory() {
		return null;
	}

	/**
	 * Get the column by its column id in the given table
	 *
	 * @param table
	 * @param columnId
	 * @return
	 */
	protected final SyTableColumn getTableColumn(JSyTable table, String columnId) {
		AbstractSimpleSpecialTableModel tableModel = (AbstractSimpleSpecialTableModel) table.getModel();
		return (SyTableColumn) table.getYColumnModel().getColumn(tableModel.findColumnIndexById(columnId), true);
	}

	protected final Map<String, Object> getValueFilters() {
		return defaultSearchComponentsPanel.getValueFilters();
	}

	protected final JSearchHeader getSearchHeader() {
		return defaultSearchComponentsPanel.getSearchHeader();
	}

	protected final DefaultTablePageComponentsPanel<E> getTablePageComponentsPanel() {
		return defaultSearchComponentsPanel.getTablePageComponentsPanel();
	}

	protected final DefaultSearchTablePageComponentsPanel<E> getDefaultSearchComponentsPanel() {
		return defaultSearchComponentsPanel;
	}

}
