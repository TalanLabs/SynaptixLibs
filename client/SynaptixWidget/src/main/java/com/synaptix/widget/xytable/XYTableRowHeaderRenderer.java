package com.synaptix.widget.xytable;

import java.awt.Component;

public interface XYTableRowHeaderRenderer {

	public Component getXYTableRowHeaderRendererComponent(JXYTable xyTable, String name, int row, boolean selected);

}
