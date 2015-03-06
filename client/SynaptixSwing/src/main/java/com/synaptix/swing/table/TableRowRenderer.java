package com.synaptix.swing.table;

import java.awt.Component;

import javax.swing.JTable;

public interface TableRowRenderer {

	public Component getTableRowRenderer(final Component c, final JTable table,
			final boolean isSelected, final boolean hasFocus, final int row,final int col);

}
