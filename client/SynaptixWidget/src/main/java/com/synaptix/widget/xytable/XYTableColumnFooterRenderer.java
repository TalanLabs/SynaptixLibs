package com.synaptix.widget.xytable;

import java.awt.Component;

public interface XYTableColumnFooterRenderer {

	public Component getXYTableColumnFooterRendererComponent(JXYTable xyTable, Object value, int column, boolean selected);

}
