package com.synaptix.swing.table;

import javax.swing.table.TableModel;

public interface SpecialTableModel extends TableModel {

	public String getColumnId(int columnIndex);

	public Object getSumValueAt(int columnIndex);

	public void setSumValueAt(Object aValue, int columnIndex);

	public boolean isExistSumValue(int columnIndex);

	public Class<?> getSumColumnClass(int columnIndex);

	public boolean isSortable(int columnIndex);

	public boolean isSearchable(int columnIndex);

	public boolean isLock(int columnIndex);

	public boolean isDefaultVisible(int columnIndex);

}
