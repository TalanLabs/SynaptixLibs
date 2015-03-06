package com.synaptix.swing.search;

public abstract class AbstractSimpleResult extends AbstractResult {

	public Object getValue(int rowIndex, int columnIndex) {
		return getValueAt(rowIndex, getColumnId(columnIndex));
	}

	public Object getSumValueAt(int columnIndex) {
		return getSumValueAt(getColumnId(columnIndex));
	}

	public abstract Object getValueAt(int rowIndex, String columnId);

	public abstract Object getSumValueAt(String columnId);

}
