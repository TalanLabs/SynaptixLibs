package com.synaptix.widget.component.view.swing.descriptor;

import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import com.synaptix.component.IComponent;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.table.AbstractSimpleSpecialTableModel;
import com.synaptix.swing.table.SyTableColumn;
import com.synaptix.widget.component.controller.context.ISearchComponentsContext;
import com.synaptix.widget.component.controller.context.ITablePageComponentsContext;
import com.synaptix.widget.component.view.swing.DefaultTablePageComponentsPanel;
import com.synaptix.widget.view.swing.descriptor.AbstractTablePageViewDescriptor;
import com.synaptix.widget.view.swing.helper.IPopupMenuActionsBuilder;
import com.synaptix.widget.view.swing.helper.IToolBarActionsBuilder;
import com.synaptix.widget.view.swing.tablemodel.IColumnTableFactory;

/**
 * Default panel descriptor with no buttons, no renderers and no specific filters
 * 
 * @author Nicolas P
 * 
 */
public class DefaultTablePagePanelDescriptor<E extends IComponent> extends AbstractTablePageViewDescriptor<E> {

	protected final ConstantsWithLookingBundle constantsWithLookingBundle;

	protected final String[] tableColumns;

	protected final String[] defaultHideTableColumns;

	private ITablePageComponentsContext tablePageComponentsContext;

	private DefaultTablePageComponentsPanel<E> defaultTablePageComponentsPanel;

	private boolean detailInitialized;

	private JComponent detailComponent;

	private boolean columnTableFactoryInitialized;

	private IColumnTableFactory<E> columnTableFactory;

	public DefaultTablePagePanelDescriptor(ConstantsWithLookingBundle constantsWithLookingBundle, String[] tableColumns) {
		this(constantsWithLookingBundle, tableColumns, null);
	}

	public DefaultTablePagePanelDescriptor(ConstantsWithLookingBundle constantsWithLookingBundle, String[] tableColumns, String[] defaultHideTableColumns) {
		super();

		this.constantsWithLookingBundle = constantsWithLookingBundle;
		this.tableColumns = tableColumns;
		this.defaultHideTableColumns = defaultHideTableColumns;

		detailInitialized = false;
	}

	@Override
	public final void setSearchComponentsContext(ISearchComponentsContext searchComponentsContext) {
		this.tablePageComponentsContext = searchComponentsContext;
	}

	@Override
	public void create() {
	}

	protected final ITablePageComponentsContext getTablePageComponentsContext() {
		return tablePageComponentsContext;
	}

	@Override
	public final ConstantsWithLookingBundle getDescriptorBundle() {
		return constantsWithLookingBundle;
	}

	@Override
	public final String[] getTableColumns() {
		return tableColumns;
	}

	@Override
	public final String[] getDefaultHideTableColumns() {
		return defaultHideTableColumns;
	}

	@Override
	public Set<String> getColumns() {
		return getTablePageComponentsPanel().getColumns();
	}

	@Override
	public boolean useAllColumns() {
		return true;
	}

	@Override
	public final void install(DefaultTablePageComponentsPanel<E> defaultTablePageComponentsPanel) {
		this.defaultTablePageComponentsPanel = defaultTablePageComponentsPanel;
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

	protected final DefaultTablePageComponentsPanel<E> getTablePageComponentsPanel() {
		return defaultTablePageComponentsPanel;
	}

	protected final E getSelectedComponent() {
		if (defaultTablePageComponentsPanel.getTable().getSelectedRowCount() == 1) {
			return defaultTablePageComponentsPanel.getSelectedComponents().get(0);
		}
		return null;
	}

	protected final List<E> getSelectedComponents() {
		return defaultTablePageComponentsPanel.getSelectedComponents();
	}
}
