package com.synaptix.widget.xytable;

import java.awt.Component;

public interface XYTableColumnHeaderRenderer {

	public Component getXYTableColumnHeaderRendererComponent(JXYTable xyTable, String name, int column, boolean selected);

}
