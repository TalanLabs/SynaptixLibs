package com.synaptix.swing.table;

import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import com.synaptix.swing.JSyTable;

public interface FilterColumn {

	public Object getFilterColumn(JSyTable table, Object filter, SyTableColumn c);

	public RowFilter<? super TableModel, ? super Integer> getRowFilter(
			JSyTable table, Object filter, SyTableColumn c);

}
