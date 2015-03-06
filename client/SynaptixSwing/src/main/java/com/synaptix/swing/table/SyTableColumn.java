package com.synaptix.swing.table;

import java.util.Comparator;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class SyTableColumn extends TableColumn {

	private static final long serialVersionUID = -2771317661479765036L;

	protected boolean lock;

	protected boolean visible;

	protected boolean defaultVisible;

	protected boolean sortable;

	protected boolean searchable;

	protected Object search;

	protected int defaultWidth;

	protected boolean existSumValue;

	protected Comparator<?> comparator;

	protected FilterColumn filter;

	protected TableCellRenderer cellSumRenderer;

	protected ExcelColumnRenderer excelColumnRenderer;

	public SyTableColumn(int modelIndex) {
		super(modelIndex);

		initializeLocalVars();
	}

	protected void initializeLocalVars() {
		defaultVisible = true;
		visible = true;
		sortable = true;
		searchable = true;
		search = null;
		defaultWidth = 75;
		existSumValue = false;
		lock = false;

		comparator = null;
		filter = null;
	}

	public Comparator<?> getComparator() {
		return comparator;
	}

	public void setComparator(Comparator<?> comparator) {
		this.comparator = comparator;
	}

	public FilterColumn getFilter() {
		return filter;
	}

	public void setFilter(FilterColumn filter) {
		this.filter = filter;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Object getSearch() {
		return search;
	}

	public void setSearch(Object search) {
		this.search = search;
	}

	public boolean isDefaultVisible() {
		return defaultVisible;
	}

	public void setDefaultVisible(boolean defaultVisible) {
		this.defaultVisible = defaultVisible;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	public boolean isSearchable() {
		return searchable;
	}

	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}

	public int getDefaultWidth() {
		return defaultWidth;
	}

	public void setDefaultWidth(int defaultWidth) {
		this.defaultWidth = defaultWidth;
	}

	public boolean isExistSumValue() {
		return existSumValue;
	}

	public void setExistSumValue(boolean existSumValue) {
		this.existSumValue = existSumValue;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
	}

	public boolean isLock() {
		return lock;
	}

	public TableCellRenderer getCellSumRenderer() {
		return cellSumRenderer;
	}

	public void setCellSumRenderer(TableCellRenderer cellSumRenderer) {
		this.cellSumRenderer = cellSumRenderer;
	}

	public ExcelColumnRenderer getExcelColumnRenderer() {
		return excelColumnRenderer;
	}

	public void setExcelColumnRenderer(ExcelColumnRenderer excelColumnRenderer) {
		this.excelColumnRenderer = excelColumnRenderer;
	}
}
