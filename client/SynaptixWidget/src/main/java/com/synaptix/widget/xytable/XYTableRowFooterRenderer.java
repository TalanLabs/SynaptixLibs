package com.synaptix.widget.xytable;

import java.awt.Component;

public interface XYTableRowFooterRenderer {

	public Component getXYTableRowFooterRendererComponent(JXYTable xyTable, Object value, int row, boolean selected);

}
