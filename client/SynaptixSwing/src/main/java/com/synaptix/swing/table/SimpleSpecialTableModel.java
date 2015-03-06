package com.synaptix.swing.table;


public interface SimpleSpecialTableModel extends SpecialTableModel {

	public Column getColumn(int index);

	public Object getValueAt(int rowIndex, String columnId);

	public void setValueAt(Object aValue, int rowIndex, String columnId);

	public Object getSumValueAt(String columnId);

	public void setSumValueAt(Object aValue, String columnId);

}
