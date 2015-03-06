package com.synaptix.widget.xytable;

public interface XYTableModel {

	public int getColumnCount();

	public String getColumnName(int column);

	public Object getColumnValue(int column);

	public int getRowCount();

	public String getRowName(int row);

	public Object getRowValue(int row);

	public Object getValue(int column, int row);

	public void addXYTableModelListener(XYTableModelListener l);

	public void removeXYTableModelListener(XYTableModelListener l);

}
