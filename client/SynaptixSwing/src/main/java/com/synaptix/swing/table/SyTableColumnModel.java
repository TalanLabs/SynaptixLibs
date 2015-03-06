package com.synaptix.swing.table;

import java.util.List;

import javax.swing.RowSorter.SortKey;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public interface SyTableColumnModel extends TableColumnModel {

	public void visibleColumn(TableColumn column);

	public void invisibleColumn(TableColumn column);

	public TableColumn getColumn(int columnIndex, boolean includeHidden);

	public List<TableColumn> getColumns(boolean includeHidden, boolean initial);

	public int getColumnCount(boolean includeHidden);

	public void defaultColumns();

	public void save();

	public boolean isSave();

	public void load();

	public void load(int autoResizeMode, int[] positions, boolean[] visibles,
			int[] sizes, Object[] searchs, List<? extends SortKey> sortKeys);
}
