package com.synaptix.swing.search;

public interface Result {
	
	public int getColumnCount();
	
	public String getColumnId(int index);
	
	public int getRowCount();
	
	public Object getValue(int rowIndex,int columnIndex);
	
	public Object getSumValueAt(int columnIndex);
	
}
