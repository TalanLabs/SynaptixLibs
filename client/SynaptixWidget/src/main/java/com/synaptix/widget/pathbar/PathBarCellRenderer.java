package com.synaptix.widget.pathbar;

import java.awt.Component;

public interface PathBarCellRenderer {

	public Component getPathBarCellRendererComponent(JPathBar pathBar, Object value, int index, boolean selected);

}
