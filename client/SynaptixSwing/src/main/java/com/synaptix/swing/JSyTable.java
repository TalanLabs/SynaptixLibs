package com.synaptix.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.RowSorter.SortKey;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.UIResource;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.synaptix.prefs.SyPreferences;
import com.synaptix.swing.table.DialogVisibilityColumns;
import com.synaptix.swing.table.ExcelColumnRenderer;
import com.synaptix.swing.table.FilterColumn;
import com.synaptix.swing.table.Function;
import com.synaptix.swing.table.JSyColumnControl;
import com.synaptix.swing.table.JSyExportControl;
import com.synaptix.swing.table.JSyTableFooter;
import com.synaptix.swing.table.JSyTableHeader;
import com.synaptix.swing.table.JSyTableLines;
import com.synaptix.swing.table.SpecialTableModel;
import com.synaptix.swing.table.SyDefaultTableColumnModel;
import com.synaptix.swing.table.SyTableColumn;
import com.synaptix.swing.table.SyTableColumnModel;
import com.synaptix.swing.table.TableFileWriter;
import com.synaptix.swing.table.TableRowRenderer;
import com.synaptix.swing.table.filter.BooleanFilterColumn;
import com.synaptix.swing.table.filter.DateFilterColumn;
import com.synaptix.swing.table.filter.NumberFilterColumn;
import com.synaptix.swing.table.filter.StringFilterColumn;
import com.synaptix.swing.table.sort.BooleanComparator;
import com.synaptix.swing.table.sort.DateComparator;
import com.synaptix.swing.table.sort.NumberComparator;
import com.synaptix.swing.table.sort.ObjectComparator;
import com.synaptix.swing.table.sort.StringComparator;

public class JSyTable extends JTable {

	private static final long serialVersionUID = -2897228565562508348L;

	protected JSyTableLines tableLines;

	protected JSyTableFooter tableFooter;

	protected JSyColumnControl columnControl;

	protected JSyExportControl exportControl;

	protected boolean showTableLines;

	protected boolean showTableFooter;

	protected JLabel rowCountLabel;

	protected String title;

	protected JPopupMenu mPopupMenu;

	protected JMenuItem mMenuItemHide;

	protected JMenuItem mMenuItemUnfilter;

	protected JMenuItem mMenuItemUnSort;

	protected JMenuItem mMenuItemSearch;

	protected JMenuItem mMenuItemVisiblityProperties;

	protected JMenuItem mMenuExport;

	protected TableRowRenderer tableRowRenderer;

	protected SyTableColumn mCurrentColumn;

	protected Map<Class<?>, FilterColumn> mapFilterColumns;

	protected Map<Class<?>, Comparator<?>> mapComparatorColumns;

	protected Map<Class<?>, ExcelColumnRenderer> mapExcelColumns;

	protected boolean displayAllRowCount;

	public JSyTable(SpecialTableModel tm) {
		this(tm, null);
	}

	public JSyTable(SpecialTableModel tm, boolean sortable) {
		this(tm, null, sortable);
	}

	public JSyTable(SpecialTableModel tm, String title) {
		this(tm, title, true);
	}

	public JSyTable(SpecialTableModel tm, String title, boolean sortable) {
		this(tm, title, sortable, false);
	}

	/**
	 * Synaptix table
	 *
	 * @param tm       tableModel
	 * @param title    title of the table
	 * @param sortable is table sortable?
	 * @param copyLine default: ctrl+C copies the selected cell. If true, copy the entire line (default behaviour)
	 */
	@SuppressWarnings("unchecked")
	public JSyTable(SpecialTableModel tm, String title, boolean sortable, boolean copyLine) {
		super(tm);

		this.title = title;

		this.displayAllRowCount = true;

		load();

		getTableHeader().addMouseListener(new TableHeaderMouseListener());

		this.setAutoCreateRowSorter(sortable);
		if (sortable) {
			this.getRowSorter().addRowSorterListener(new TableRowSorterListener());
			TableRowSorter<TableModel> trs = (TableRowSorter<TableModel>) this.getRowSorter();
			for (int i = 0; i < this.getYColumnModel().getColumnCount(true); i++) {
				int columnIndex = this.getYColumnModel().getColumn(i, true).getModelIndex();
				trs.setComparator(i, new RowSortComparator(columnIndex));
			}
		}

		initializeFilterColumns();
		initializeComparatorColumns();
		initializeRenderer();
		initializeEditor();

		mapExcelColumns = new HashMap<Class<?>, ExcelColumnRenderer>();

		setPreferredScrollableViewportSize(new Dimension(10, 10));

		if (!copyLine) {
			// copy
			ActionListener listener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					doCopy();
				}
			};

			final KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);

			registerKeyboardAction(listener, "Copy", stroke, JComponent.WHEN_FOCUSED);
			// end of copy
		}
	}

	private void initializeRenderer() {
		super.setDefaultRenderer(boolean.class, super.getDefaultRenderer(Boolean.class));
		super.setDefaultRenderer(int.class, super.getDefaultRenderer(Integer.class));
		super.setDefaultRenderer(long.class, super.getDefaultRenderer(Long.class));
		super.setDefaultRenderer(double.class, super.getDefaultRenderer(Double.class));
		super.setDefaultRenderer(float.class, super.getDefaultRenderer(Float.class));
		super.setDefaultRenderer(short.class, super.getDefaultRenderer(Short.class));
		super.setDefaultRenderer(byte.class, super.getDefaultRenderer(Byte.class));
		super.setDefaultRenderer(char.class, super.getDefaultRenderer(Character.class));
	}

	private void initializeEditor() {
		super.setDefaultEditor(boolean.class, super.getDefaultEditor(Boolean.class));
		super.setDefaultEditor(int.class, super.getDefaultEditor(Integer.class));
		super.setDefaultEditor(long.class, super.getDefaultEditor(Long.class));
		super.setDefaultEditor(double.class, super.getDefaultEditor(Double.class));
		super.setDefaultEditor(float.class, super.getDefaultEditor(Float.class));
		super.setDefaultEditor(short.class, super.getDefaultEditor(Short.class));
		super.setDefaultEditor(byte.class, super.getDefaultEditor(Byte.class));
		super.setDefaultEditor(char.class, super.getDefaultEditor(Character.class));
	}

	private void initializeFilterColumns() {
		mapFilterColumns = new HashMap<Class<?>, FilterColumn>();
		mapFilterColumns.put(Object.class, new StringFilterColumn());
		mapFilterColumns.put(String.class, new StringFilterColumn());
		mapFilterColumns.put(Date.class, new DateFilterColumn());
		mapFilterColumns.put(Integer.class, new NumberFilterColumn());
		mapFilterColumns.put(Double.class, new NumberFilterColumn());
		mapFilterColumns.put(Float.class, new NumberFilterColumn());
		mapFilterColumns.put(Number.class, new NumberFilterColumn());
		mapFilterColumns.put(BigDecimal.class, new NumberFilterColumn());
		mapFilterColumns.put(BigInteger.class, new NumberFilterColumn());
		mapFilterColumns.put(Byte.class, new NumberFilterColumn());
		mapFilterColumns.put(Long.class, new NumberFilterColumn());
		mapFilterColumns.put(Short.class, new NumberFilterColumn());
		mapFilterColumns.put(Boolean.class, new BooleanFilterColumn());
		mapFilterColumns.put(boolean.class, new BooleanFilterColumn());
		mapFilterColumns.put(int.class, new NumberFilterColumn());
		mapFilterColumns.put(long.class, new NumberFilterColumn());
		mapFilterColumns.put(double.class, new NumberFilterColumn());
		mapFilterColumns.put(float.class, new NumberFilterColumn());
		mapFilterColumns.put(short.class, new NumberFilterColumn());
		mapFilterColumns.put(byte.class, new NumberFilterColumn());
		mapFilterColumns.put(char.class, new StringFilterColumn());
	}

	private void initializeComparatorColumns() {
		mapComparatorColumns = new HashMap<Class<?>, Comparator<?>>();
		mapComparatorColumns.put(Object.class, new ObjectComparator());
		mapComparatorColumns.put(String.class, new StringComparator());
		mapComparatorColumns.put(Date.class, new DateComparator());
		mapComparatorColumns.put(Integer.class, new NumberComparator());
		mapComparatorColumns.put(Double.class, new NumberComparator());
		mapComparatorColumns.put(Float.class, new NumberComparator());
		mapComparatorColumns.put(Number.class, new NumberComparator());
		mapComparatorColumns.put(BigDecimal.class, new NumberComparator());
		mapComparatorColumns.put(BigInteger.class, new NumberComparator());
		mapComparatorColumns.put(Byte.class, new NumberComparator());
		mapComparatorColumns.put(Long.class, new NumberComparator());
		mapComparatorColumns.put(Short.class, new NumberComparator());
		mapComparatorColumns.put(Boolean.class, new BooleanComparator());

		mapComparatorColumns.put(boolean.class, new BooleanComparator());
		mapComparatorColumns.put(int.class, new NumberComparator());
		mapComparatorColumns.put(long.class, new NumberComparator());
		mapComparatorColumns.put(double.class, new NumberComparator());
		mapComparatorColumns.put(float.class, new NumberComparator());
		mapComparatorColumns.put(short.class, new NumberComparator());
		mapComparatorColumns.put(byte.class, new NumberComparator());
		mapComparatorColumns.put(char.class, new StringComparator());
	}

	public void load() {
		getYColumnModel().load();

		getColumnControl().load();

		SyPreferences prefs = SyPreferences.getPreferences();
		setDisplayAllRowCount(prefs.getBoolean(title + "_displayAllRowCount", true));
	}

	public void save() {
		getYColumnModel().save();
	}

	public boolean isSave() {
		return getYColumnModel().isSave();
	}

	public void addDefaultPerspective(String name, boolean[] visibles) {
		addDefaultPerspective(name, JTable.AUTO_RESIZE_NEXT_COLUMN, visibles);
	}

	public void addDefaultPerspective(String name, int autoResizeMode, boolean[] visibles) {
		int[] positions = new int[visibles.length];
		for (int i = 0; i < positions.length; i++) {
			positions[i] = i;
		}

		addDefaultPerspective(name, autoResizeMode, positions, visibles);
	}

	public void addDefaultPerspective(String name, int[] positions, boolean[] visibles) {
		addDefaultPerspective(name, JTable.AUTO_RESIZE_NEXT_COLUMN, positions, visibles);
	}

	public void addDefaultPerspective(String name, int autoResizeMode, int[] positions, boolean[] visibles) {
		SyTableColumnModel ytcm = (SyTableColumnModel) getColumnModel();

		List<TableColumn> initialColumns = ytcm.getColumns(true, true);

		int[] sizes = new int[visibles.length];
		for (int i = 0; i < sizes.length; i++) {
			sizes[i] = initialColumns.get(i).getPreferredWidth();
		}
		addDefaultPerspective(name, autoResizeMode, positions, visibles, sizes);
	}

	public void addDefaultPerspective(String name, int[] positions, boolean[] visibles, int[] sizes) {
		addDefaultPerspective(name, JTable.AUTO_RESIZE_NEXT_COLUMN, positions, visibles, sizes);
	}

	public void addDefaultPerspective(String name, int autoResizeMode, int[] positions, boolean[] visibles, int[] sizes) {
		Object[] searchs = new Object[visibles.length];
		for (int i = 0; i < sizes.length; i++) {
			searchs[i] = null;
		}
		addDefaultPerspective(name, autoResizeMode, positions, visibles, sizes, searchs, null);
	}

	public void addDefaultPerspective(String name, int autoResizeMode, int[] positions, boolean[] visibles, int[] sizes, Object[] searchs) {
		addDefaultPerspective(name, autoResizeMode, positions, visibles, sizes, searchs);
	}

	public void addDefaultPerspective(String name, int autoResizeMode, int[] positions, boolean[] visibles, int[] sizes, Object[] searchs, List<? extends SortKey> sortKeys) {
		getColumnControl().addDefaultPerspective(name, autoResizeMode, positions, visibles, sizes, searchs, sortKeys);
	}

	public boolean isShowToolTipsColumns() {
		return ((JSyTableHeader) getTableHeader()).isShowToolTips();
	}

	public void setShowToolTipsColumns(boolean b) {
		((JSyTableHeader) getTableHeader()).setShowToolTips(b);
	}

	public boolean isDisplayAllRowCount() {
		return displayAllRowCount;
	}

	public void setDisplayAllRowCount(boolean displayAllRowCount) {
		this.displayAllRowCount = displayAllRowCount;

		updateRowCountLabel();

		SyPreferences prefs = SyPreferences.getPreferences();
		prefs.putBoolean(title + "_displayAllRowCount", displayAllRowCount);
	}

	@Override
	protected void initializeLocalVars() {
		super.initializeLocalVars();

		setTableLines(createDefaultTableLines());
		setRowCountLabel(createRowCountLabel());
		setColumnControl(createDefaultColumnControl());
		setExportControl(createDefaultExportControl());

		setTableFooter(createDefaultTableFooter());

		setShowTableLines(false);
		setShowTableFooter(false);

		updateRowCountLabel();

		createPopupMenu();
	}

	protected void createPopupMenu() {
		mPopupMenu = new JPopupMenu();

		mMenuItemHide = new JMenuItem();
		mMenuItemHide.setText(SwingMessages.getString("JSyTable.0")); //$NON-NLS-1$
		mMenuItemHide.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hideColumn(mCurrentColumn);
			}
		});

		mMenuItemSearch = new JMenuItem();
		mMenuItemSearch.setText(SwingMessages.getString("JSyTable.1")); //$NON-NLS-1$
		mMenuItemSearch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showDialogFilter(mCurrentColumn);
			}
		});

		mMenuItemUnfilter = new JMenuItem();
		mMenuItemUnfilter.setText(SwingMessages.getString("JSyTable.2")); //$NON-NLS-1$
		mMenuItemUnfilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeFilter(mCurrentColumn);
			}
		});

		mMenuItemUnSort = new JMenuItem();
		mMenuItemUnSort.setText(SwingMessages.getString("JSyTable.3")); //$NON-NLS-1$
		mMenuItemUnSort.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeSort(mCurrentColumn);
			}
		});

		mMenuItemVisiblityProperties = new JMenuItem();
		mMenuItemVisiblityProperties.setText(SwingMessages.getString("JSyTable.4")); //$NON-NLS-1$
		mMenuItemVisiblityProperties.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showDialogVisibility();
			}
		});

		mMenuExport = new JMenuItem();
		mMenuExport.setText(SwingMessages.getString("JSyTable.5")); //$NON-NLS-1$
		mMenuExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent pEvent) {
				exportTable();
			}

		});

		mPopupMenu.add(mMenuItemSearch);
		mPopupMenu.add(mMenuItemUnfilter);
		mPopupMenu.addSeparator();
		mPopupMenu.add(mMenuItemUnSort);
		mPopupMenu.addSeparator();
		mPopupMenu.add(mMenuItemHide);
		mPopupMenu.add(mMenuItemVisiblityProperties);
		mPopupMenu.addSeparator();
		mPopupMenu.add(mMenuExport);

		mCurrentColumn = null;

	}

	public Class<?> getSumColumnClass(int column) {
		return ((SpecialTableModel) getModel()).getSumColumnClass(convertColumnIndexToModel(column));
	}

	public TableCellRenderer getCellSumRenderer(int column) {
		SyTableColumn tableColumn = (SyTableColumn) getColumnModel().getColumn(column);
		TableCellRenderer renderer = tableColumn.getCellSumRenderer();
		if (renderer == null) {
			renderer = getDefaultRenderer(getSumColumnClass(column));
		}
		return renderer;
	}

	public Comparator<?> getDefaultComparatorColumn(Class<?> columnClass) {
		if (columnClass == null) {
			return null;
		} else {
			Comparator<?> comparator = mapComparatorColumns.get(columnClass);
			if (comparator != null) {
				return comparator;
			} else {
				return getDefaultComparatorColumn(columnClass.getSuperclass());
			}
		}
	}

	public Comparator<?> getComparatorColumn(int column) {
		SyTableColumnModel tcm = (SyTableColumnModel) getColumnModel();
		SyTableColumn tableColumn = (SyTableColumn) tcm.getColumn(column, true);
		Comparator<?> comparator = tableColumn.getComparator();
		if (comparator == null) {
			comparator = getDefaultComparatorColumn(getModel().getColumnClass(tableColumn.getModelIndex()));
		}
		return comparator;
	}

	public void setDefaultComparatorColumn(Class<?> columnClass, Comparator<?> comparatorColumn) {
		mapComparatorColumns.put(columnClass, comparatorColumn);
	}

	public FilterColumn getDefaultFilterColumn(Class<?> columnClass) {
		if (columnClass == null) {
			return null;
		} else {
			FilterColumn fc = mapFilterColumns.get(columnClass);
			if (fc != null) {
				return fc;
			} else {
				return getDefaultFilterColumn(columnClass.getSuperclass());
			}
		}
	}

	public FilterColumn getFilterColumn(int column) {
		SyTableColumn tableColumn = (SyTableColumn) getColumnModel().getColumn(column);
		FilterColumn fc = tableColumn.getFilter();
		if (fc == null) {
			fc = getDefaultFilterColumn(getColumnClass(column));
		}
		return fc;
	}

	public void setDefaultFilterColumn(Class<?> columnClass, FilterColumn filterColumn) {
		mapFilterColumns.put(columnClass, filterColumn);
	}

	/**
	 * Définie un excel renderer pour la class
	 *
	 * @param columnClass
	 * @param excelColumnRenderer
	 */
	public void setDefaultExcelColumnRenderer(Class<?> columnClass, ExcelColumnRenderer excelColumnRenderer) {
		mapExcelColumns.put(columnClass, excelColumnRenderer);
	}

	/**
	 * Renvoie le renderer pour l'excel par defaut pour la class
	 *
	 * @param columnClass
	 * @return
	 */
	public ExcelColumnRenderer getDefaultExcelColumnRenderer(Class<?> columnClass) {
		if (columnClass == null) {
			return null;
		} else {
			ExcelColumnRenderer fc = mapExcelColumns.get(columnClass);
			if (fc != null) {
				return fc;
			} else {
				return getDefaultExcelColumnRenderer(columnClass.getSuperclass());
			}
		}
	}

	/**
	 * Renvoie le renderer excel pour la colonne affiché dans la vue
	 *
	 * @param column
	 * @return
	 */
	public ExcelColumnRenderer getExcelColumnRenderer(int column) {
		SyTableColumn tableColumn = (SyTableColumn) getColumnModel().getColumn(column);
		ExcelColumnRenderer fc = tableColumn.getExcelColumnRenderer();
		if (fc == null) {
			fc = getDefaultExcelColumnRenderer(getColumnClass(column));
		}
		return fc;
	}

	private void showDialogFilter(SyTableColumn c) {
		FilterColumn fc = c.getFilter();
		if (fc == null) {
			Class<?> columnClass = getModel().getColumnClass(c.getModelIndex());
			fc = getDefaultFilterColumn(columnClass);

			if (fc == null) {
				fc = mapFilterColumns.get(Object.class);
			}
		}

		Object oldValue = c.getSearch();
		Object value = fc.getFilterColumn(this, oldValue, c);

		c.setSearch(value);
		reconstructRowFilter();
		resizeAndRepaint();
	}

	public void setColumnFilter(int columnIndex, Object search) {
		SyTableColumn c = (SyTableColumn) this.getColumnModel().getColumn(columnIndex);
		c.setSearch(search);

		reconstructRowFilter();
		resizeAndRepaint();
	}

	public Object getColumnFilter(int columnIndex) {
		SyTableColumn c = (SyTableColumn) this.getColumnModel().getColumn(columnIndex);
		return c.getSearch();
	}

	@SuppressWarnings("unchecked")
	public void reconstructRowFilter() {
		TableRowSorter<? extends TableModel> sorter = (TableRowSorter<TableModel>) this.getRowSorter();
		List<RowFilter<? super TableModel, ? super Integer>> list = new ArrayList<RowFilter<? super TableModel, ? super Integer>>();

		for (int i = 0; i < this.getColumnModel().getColumnCount(); i++) {
			SyTableColumn c = (SyTableColumn) this.getColumnModel().getColumn(i);
			FilterColumn fc = getFilterColumn(i);
			if (c.getSearch() != null) {
				list.add(fc.getRowFilter(this, c.getSearch(), c));
			}
		}
		if (sorter != null) {
			RowFilter<? super TableModel, ? super Integer> rowFilterAnd = RowFilter.andFilter(list);
			sorter.setRowFilter(rowFilterAnd);
		}

		updateRowCountLabel();
	}

	private void removeSort(SyTableColumn c) {
		if (this.getRowSorter() != null) {
			List<SortKey> l1 = new ArrayList<SortKey>();
			for (SortKey sortKey : getRowSorter().getSortKeys()) {
				if (sortKey.getColumn() != c.getModelIndex()) {
					l1.add(sortKey);
				}
			}
			this.getRowSorter().setSortKeys(l1);
		}
	}

	private void removeFilter(SyTableColumn c) {
		c.setSearch(null);

		reconstructRowFilter();

		resizeAndRepaint();
	}

	private void showDialogVisibility() {
		DialogVisibilityColumns dialog = new DialogVisibilityColumns(this);
		dialog.showDialog(this);
	}

	private void hideColumn(SyTableColumn c) {
		SyTableColumnModel ytcm = (SyTableColumnModel) this.getColumnModel();
		if (c.isVisible()) {
			ytcm.invisibleColumn(c);
		} else {
			ytcm.visibleColumn(c);
		}
	}

	public void exportTable() {
		TableFileWriter t = new TableFileWriter(this);
		t.export();
	}

	@Override
	protected JTableHeader createDefaultTableHeader() {
		return new JSyTableHeader(columnModel);
	}

	protected JSyTableLines createDefaultTableLines() {
		return new JSyTableLines();
	}

	protected JSyTableFooter createDefaultTableFooter() {
		return new JSyTableFooter();
	}

	protected JSyColumnControl createDefaultColumnControl() {
		return new JSyColumnControl();
	}

	protected JSyExportControl createDefaultExportControl() {
		return new JSyExportControl();
	}

	protected JLabel createRowCountLabel() {
		JLabel label = new JLabel("VIDE", JLabel.CENTER); //$NON-NLS-1$
		label.setOpaque(true);
		label.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		// label.setBorder(UIManager.getBorder("TableHeader.cellBorder"));

		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setDisplayAllRowCount(!isDisplayAllRowCount());
			}
		});

		return label;
	}

	public JSyTableFooter getTableFooter() {
		return tableFooter;
	}

	public void setTableFooter(JSyTableFooter tableFooter) {
		if (this.tableFooter != tableFooter) {
			JSyTableFooter old = this.tableFooter;
			// Release the old header
			if (old != null) {
				old.setTable(null);
			}
			this.tableFooter = tableFooter;
			if (tableFooter != null) {
				tableFooter.setTable(this);
			}
			firePropertyChange("tableFooter", old, tableFooter); //$NON-NLS-1$
		}
	}

	public JSyTableLines getTableLines() {
		return tableLines;
	}

	public void setTableLines(JSyTableLines tableLines) {
		if (this.tableLines != tableLines) {
			JSyTableLines old = this.tableLines;
			if (old != null) {
				old.setTable(null);
			}
			this.tableLines = tableLines;
			if (tableLines != null) {
				tableLines.setTable(this);
			}
			firePropertyChange("tableLines", old, tableLines); //$NON-NLS-1$
		}
	}

	public JSyColumnControl getColumnControl() {
		return columnControl;
	}

	public void setColumnControl(JSyColumnControl columnControl) {
		if (this.columnControl != columnControl) {
			JSyColumnControl old = this.columnControl;
			if (old != null) {
				old.setTable(null);
			}
			this.columnControl = columnControl;
			if (columnControl != null) {
				columnControl.setTable(this);
			}
			firePropertyChange("columnControl", old, columnControl); //$NON-NLS-1$
		}
	}

	public JSyExportControl getExportControl() {
		return exportControl;
	}

	public void setExportControl(JSyExportControl exportControl) {
		if (this.exportControl != exportControl) {
			JSyExportControl old = this.exportControl;
			if (old != null) {
				old.setTable(null);
			}
			this.exportControl = exportControl;
			if (exportControl != null) {
				exportControl.setTable(this);
			}
			firePropertyChange("exportControl", old, exportControl); //$NON-NLS-1$
		}
	}

	public JLabel getRowCountLabel() {
		return rowCountLabel;
	}

	public void setRowCountLabel(JLabel rowCountLabel) {
		if (this.rowCountLabel != rowCountLabel) {
			JLabel old = this.rowCountLabel;
			this.rowCountLabel = rowCountLabel;
			firePropertyChange("rowCountLabel", old, rowCountLabel); //$NON-NLS-1$
		}
	}

	private JLabel createLabelWithBorder(String text) {
		JLabel l = new JLabel(text, JLabel.CENTER);
		l.setOpaque(true);
		l.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		return l;
	}

	@Override
	protected void configureEnclosingScrollPane() {
		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JSyScrollPane) {
				JSyScrollPane scrollPane = (JSyScrollPane) gp;

				JViewport viewport = scrollPane.getViewport();
				if (viewport == null || viewport.getView() != this) {
					return;
				}
				scrollPane.setColumnHeaderView(getTableHeader());

				scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

				if (showTableLines) {
					scrollPane.setRowHeaderView(getTableLines());

					scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, createLabelWithBorder(SwingMessages.getString("JSyTable.12"))); //$NON-NLS-1$
					scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER, getRowCountLabel());
					scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				} else {
					scrollPane.setRowHeaderView(null);

					scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, null);
					scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER, null);
				}

				scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, getColumnControl());

				if (showTableFooter) {
					scrollPane.setColumnFooterView(getTableFooter());
					scrollPane.setCorner(JSyScrollPane.FOOTER_LEFT_CORNER, createLabelWithBorder(SwingMessages.getString("JSyTable.13"))); //$NON-NLS-1$
					scrollPane.setCorner(JSyScrollPane.FOOTER_RIGHT_CORNER, getExportControl());
				} else {
					scrollPane.setColumnFooterView(null);
					scrollPane.setCorner(JSyScrollPane.FOOTER_LEFT_CORNER, null);

					scrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, getExportControl());
				}

				Border border = scrollPane.getBorder();
				if (border == null || border instanceof UIResource) {
					scrollPane.setBorder(UIManager.getBorder("Table.scrollPaneBorder")); //$NON-NLS-1$
				}

			}
		}
	}

	@Override
	protected void unconfigureEnclosingScrollPane() {
		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JSyScrollPane) {
				JSyScrollPane scrollPane = (JSyScrollPane) gp;

				JViewport viewport = scrollPane.getViewport();
				if (viewport == null || viewport.getView() != this) {
					return;
				}
				scrollPane.setColumnHeaderView(null);
				scrollPane.setRowHeaderView(null);

				scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, null);
				scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER, null);

				scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, null);

				scrollPane.setColumnFooterView(null);
				scrollPane.setCorner(JSyScrollPane.FOOTER_LEFT_CORNER, null);

				scrollPane.setCorner(JSyScrollPane.FOOTER_RIGHT_CORNER, null);
			}
		}
	}

	private void updateRowCountLabel() {
		String text = ""; //$NON-NLS-1$
		TableModel model = this.getModel();
		if (model != null) {
			int rowCount;
			if (displayAllRowCount) {
				rowCount = model.getRowCount();
			} else {
				rowCount = this.getRowCount();
			}
			text = String.valueOf(rowCount);
		}
		JLabel label = getRowCountLabel();
		if (label != null) {
			label.setText(text);
		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);

		if (tableFooter != null) {
			tableFooter.revalidate();
			tableFooter.repaint();
		}

		updateRowCountLabel();
	}

	@Override
	public void columnMoved(TableColumnModelEvent e) {
		super.columnMoved(e);
		resizeAndRepaint();
	}

	public Object getSumValueAt(int column) {
		Object res = null;
		if (getModel() instanceof SpecialTableModel) {
			res = ((SpecialTableModel) getModel()).getSumValueAt(convertColumnIndexToModel(column));
		}
		return res;
	}

	@Override
	public void updateUI() {
		if (tableLines != null && tableLines.getParent() == null) {
			tableLines.updateUI();
		}
		super.updateUI();
	}

	@Override
	protected TableColumnModel createDefaultColumnModel() {
		return new SyDefaultTableColumnModel(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void resizeAndRepaint() {
		TableRowSorter<TableModel> trs = (TableRowSorter<TableModel>) this.getRowSorter();
		if (trs != null) {
			TableColumnModel cm = getColumnModel();
			for (int i = 0; i < cm.getColumnCount(); i++) {
				SyTableColumn stc = (SyTableColumn) cm.getColumn(i);
				int c = this.convertColumnIndexToModel(i);
				if (c >= 0 && c < getModel().getColumnCount()) {
					trs.setSortable(c, stc.isSortable());
				}
			}
		}
		if (tableLines != null) {
			tableLines.revalidate();
			tableLines.repaint();
		}

		if (tableFooter != null) {
			tableFooter.revalidate();
			tableFooter.repaint();
		}

		if (tableHeader != null) {
			tableHeader.revalidate();
			tableHeader.repaint();
		}

		revalidate();
		repaint();
	}

	@Override
	public void createDefaultColumnsFromModel() {
		SpecialTableModel m = (SpecialTableModel) getModel();
		if (m != null) {
			// Remove any current columns
			TableColumnModel cm = getColumnModel();
			while (cm.getColumnCount() > 0) {
				cm.removeColumn(cm.getColumn(0));
			}

			// Create new columns from the data model info
			for (int i = 0; i < m.getColumnCount(); i++) {
				SyTableColumn newColumn = new SyTableColumn(i);
				newColumn.setSortable(m.isSortable(i));
				newColumn.setSearchable(m.isSearchable(i));
				newColumn.setExistSumValue(m.isExistSumValue(i));
				newColumn.setLock(m.isLock(i));
				newColumn.setDefaultVisible(m.isDefaultVisible(i));
				newColumn.setIdentifier(m.getColumnId(i));

				addColumn(newColumn);
			}
		}
	}

	public boolean isRowResinzingAllowed() {
		if (tableLines != null) {
			return tableLines.isRowResinzingAllowed();
		}
		return false;
	}

	public void setRowResinzingAllowed(boolean rowResinzingAllowed) {
		if (tableLines != null) {
			tableLines.setRowResinzingAllowed(rowResinzingAllowed);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		super.valueChanged(e);
		if (tableLines != null) {
			tableLines.repaint();
		}
	}

	public boolean isShowTableFooter() {
		return showTableFooter;
	}

	public void setShowTableFooter(boolean showTableFooter) {
		if (this.showTableFooter != showTableFooter) {
			boolean old = this.showTableFooter;
			this.showTableFooter = showTableFooter;
			configureEnclosingScrollPane();
			resizeAndRepaint();
			firePropertyChange("showTableFooter", old, showTableFooter); //$NON-NLS-1$
		}
	}

	public boolean isShowTableLines() {
		return showTableLines;
	}

	public void setShowTableLines(boolean showTableLines) {
		if (this.showTableLines != showTableLines) {
			boolean old = this.showTableLines;
			this.showTableLines = showTableLines;
			configureEnclosingScrollPane();
			resizeAndRepaint();
			firePropertyChange("showTableLines", old, showTableLines); //$NON-NLS-1$
		}
	}

	public String getTitle() {
		return title;
	}

	public SyTableColumnModel getYColumnModel() {
		return (SyTableColumnModel) columnModel;
	}

	public TableRowRenderer getTableRowRenderer() {
		return tableRowRenderer;
	}

	public void setTableRowRenderer(TableRowRenderer tableRowRenderer) {
		this.tableRowRenderer = tableRowRenderer;
	}

	private void showPopupMenu(MouseEvent e) {
		SyTableColumnModel tcm = (SyTableColumnModel) getColumnModel();

		int i = columnAtPoint(e.getPoint());
		if (i != -1) {
			SpecialTableModel model = (SpecialTableModel) this.getModel();
			mCurrentColumn = (SyTableColumn) this.getColumn(model.getColumnId(convertColumnIndexToModel(i)));
		} else {
			mCurrentColumn = null;
		}

		if (mCurrentColumn != null) {
			String name = this.getModel().getColumnName(mCurrentColumn.getModelIndex());

			mMenuItemHide.setText(MessageFormat.format(SwingMessages.getString("JSyTable.0"), name)); //$NON-NLS-1$

			if (tcm.getColumnCount() == 1 || mCurrentColumn.isLock()) {
				mMenuItemHide.setEnabled(false);
			} else {
				mMenuItemHide.setEnabled(true);
			}

			if (mCurrentColumn.isSearchable()) {
				if (mCurrentColumn.getSearch() != null) {
					mMenuItemSearch.setText(MessageFormat.format(SwingMessages.getString("JSyTable.20"), mCurrentColumn.getSearch())); //$NON-NLS-1$
					mMenuItemUnfilter.setEnabled(true);
				} else {
					mMenuItemSearch.setText(SwingMessages.getString("JSyTable.1")); //$NON-NLS-1$
					mMenuItemUnfilter.setEnabled(false);
				}
			} else {
				mMenuItemSearch.setEnabled(false);
				mMenuItemUnfilter.setEnabled(false);
			}

			mMenuItemUnSort.setEnabled(false);
			if (this.getRowSorter() != null) {
				for (SortKey sortKey : this.getRowSorter().getSortKeys()) {
					if (sortKey.getColumn() == mCurrentColumn.getModelIndex()) {
						mMenuItemUnSort.setEnabled(true);
					}
				}
			}

			mPopupMenu.pack();
			mPopupMenu.show(this.getTableHeader(), e.getPoint().x, e.getPoint().y);
		}
	}

	public Object calculateSumValueAt(int columnIndex) {
		SpecialTableModel model = (SpecialTableModel) this.getModel();
		int col = this.convertColumnIndexToModel(columnIndex);

		Object value = model.getSumValueAt(col);
		if (value instanceof Function) {
			Function f = (Function) value;

			int[] rowVisibles = new int[this.getRowCount()];
			for (int i = 0; i < rowVisibles.length; i++) {
				rowVisibles[i] = this.convertRowIndexToModel(i);
			}

			return f.calculate(this, col);
		}
		return value;
	}

	public Component prepareFooterRenderer(TableCellRenderer renderer, int column) {
		Object value = calculateSumValueAt(column);

		Component c = renderer.getTableCellRendererComponent(this, value, false, false, 0, column);
		c.setBackground(this.getBackground());
		c.setForeground(this.getForeground());
		c.setFont(this.getFont());
		return c;
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Object value = getValueAt(row, column);

		boolean isSelected = false;
		boolean hasFocus = false;

		// Only indicate the selection and focused cell if not printing
		if (!isPaintingForPrint()) {
			isSelected = isCellSelected(row, column);

			boolean rowIsLead = (selectionModel.getLeadSelectionIndex() == row);
			boolean colIsLead = (columnModel.getSelectionModel().getLeadSelectionIndex() == column);

			hasFocus = (rowIsLead && colIsLead) && isFocusOwner();
		}

		Component c = renderer.getTableCellRendererComponent(this, value, isSelected, hasFocus, row, column);
		if (getTableRowRenderer() != null) {
			c = getTableRowRenderer().getTableRowRenderer(c, JSyTable.this, isCellSelected(row, column), false, row, column);
		}
		return c;
	}

	private void doCopy() {
		int col = getSelectedColumn();
		int row = getSelectedRow();
		if (col != -1 && row != -1) {
			TableCellRenderer cellRenderer = getCellRenderer(row, col);
			Object value = getValueAt(row, col);
			String data = null;
			Component component = cellRenderer.getTableCellRendererComponent(this, value, true, true, row, col);
			if ((component != null) && JLabel.class.isAssignableFrom(component.getClass())) {
				data = ((JLabel) component).getText();
			} else if (value == null) {
				data = "";
			} else {
				data = value.toString();
			}

			final StringSelection selection = new StringSelection(data);

			final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, selection);
		}
	}

	private class TableHeaderMouseListener extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				showPopupMenu(e);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				showPopupMenu(e);
			}
		}
	}

	private class TableRowSorterListener implements RowSorterListener {
		@Override
		public void sorterChanged(RowSorterEvent e) {

			// List<SortKey> sortKeys = new ArrayList<SortKey>();
			// for(SortKey sortKey : getRowSorter().getSortKeys()) {
			// sortKeys.add(sortKey);
			// }
			// getRowSorter().setSortKeys(sortKeys);
		}
	}

	private final class RowSortComparator implements Comparator<Object> {

		private int columnIndex;

		public RowSortComparator(int columnIndex) {
			this.columnIndex = columnIndex;
		}

		@Override
		@SuppressWarnings("unchecked")
		public int compare(Object o1, Object o2) {
			int res = 0;
			if (o1 == null && o2 == null) {
				res = 0;
			} else if (o1 == null && o2 != null) {
				res = -1;
			} else if (o1 != null && o2 == null) {
				res = 1;
			} else {
				int column = -1;
				for (int i = 0; i < getYColumnModel().getColumnCount(true); i++) {
					SyTableColumn stc = (SyTableColumn) getYColumnModel().getColumn(i, true);
					if (stc.getModelIndex() == columnIndex) {
						column = i;
					}
				}
				if (column >= 0) {
					Comparator<Object> comparator = (Comparator<Object>) getComparatorColumn(column);
					if (comparator != null) {
						res = comparator.compare(o1, o2);
					}
				}
			}
			return res;
		}
	}
}
