package com.synaptix.swing.search;

import com.synaptix.swing.table.Column;

public interface SearchTableModel {

	public int getColumnCount();

	public Column getColumn(int index);
	
}
