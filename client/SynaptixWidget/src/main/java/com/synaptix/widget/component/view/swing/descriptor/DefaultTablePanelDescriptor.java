package com.synaptix.widget.component.view.swing.descriptor;

import java.util.List;

import javax.swing.JComponent;

import com.synaptix.component.IComponent;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.table.AbstractSimpleSpecialTableModel;
import com.synaptix.swing.table.SyTableColumn;
import com.synaptix.widget.component.controller.context.ISearchComponentsContext;
import com.synaptix.widget.component.view.swing.DefaultTableComponentsPanel;
import com.synaptix.widget.view.swing.descriptor.AbstractTableViewDescriptor;
import com.synaptix.widget.view.swing.helper.IPopupMenuActionsBuilder;
import com.synaptix.widget.view.swing.helper.IToolBarActionsBuilder;
import com.synaptix.widget.view.swing.tablemodel.IColumnTableFactory;

/**
 * Default panel descriptor with no buttons, no renderers and no specific filters
 * 
 * @author Nicolas P
 * 
 */
public class DefaultTablePanelDescriptor<E extends IComponent> extends AbstractTableViewDescriptor<E> {

	protected final ConstantsWithLookingBundle constantsWithLookingBundle;

	protected final String[] tableColumns;

	protected final String[] defaultHideTableColumns;

	private ISearchComponentsContext searchComponentsContext;

	private DefaultTableComponentsPanel<E> defaultTableComponentsPanel;

	private boolean detailInitialized;

	private JComponent detailComponent;

	private boolean columnTableFactoryInitialized;

	private IColumnTableFactory<E> columnTableFactory;

	public DefaultTablePanelDescriptor(ConstantsWithLookingBundle constantsWithLookingBundle, String[] tableColumns) {
		this(constantsWithLookingBundle, tableColumns, null);
	}

	public DefaultTablePanelDescriptor(ConstantsWithLookingBundle constantsWithLookingBundle, String[] tableColumns, String[] defaultHideTableColumns) {
		super();

		this.constantsWithLookingBundle = constantsWithLookingBundle;
		this.tableColumns = tableColumns;
		this.defaultHideTableColumns = defaultHideTableColumns;

		detailInitialized = false;
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
	public String[] getTableColumns() {
		return tableColumns;
	}

	@Override
	public String[] getDefaultHideTableColumns() {
		return defaultHideTableColumns;
	}

	@Override
	public void install(DefaultTableComponentsPanel<E> defaultTableComponentsPanel) {
		this.defaultTableComponentsPanel = defaultTableComponentsPanel;
	}

	@Override
	public void initialize() {
	}

	@Override
	public void installTable(JSyTable table) {
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

	protected final DefaultTableComponentsPanel<E> getDefaultTableComponentsPanel() {
		return defaultTableComponentsPanel;
	}

	protected final E getSelectedComponent() {
		if (getDefaultTableComponentsPanel().getTable().getSelectedRowCount() == 1) {
			return getDefaultTableComponentsPanel().getSelectedComponents().get(0);
		}
		return null;
	}

	protected final List<E> getSelectedComponents() {
		return getDefaultTableComponentsPanel().getSelectedComponents();
	}
}
