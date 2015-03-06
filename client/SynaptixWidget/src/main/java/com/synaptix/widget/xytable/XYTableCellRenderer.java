package com.synaptix.widget.xytable;

import java.awt.Component;

public interface XYTableCellRenderer {

	public Component getXYTableCellRendererComponent(JXYTable xyTable, Object value, int column, int row, boolean selected);

}
