package com.synaptix.widget.renderer.view.swing;

import javax.swing.JTable;

public interface ITableToString {

	public String getString(JTable table, Object value, int row, int column);

}
