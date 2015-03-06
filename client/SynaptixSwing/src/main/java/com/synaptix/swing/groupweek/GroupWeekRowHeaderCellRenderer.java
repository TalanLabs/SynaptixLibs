package com.synaptix.swing.groupweek;

import java.awt.Component;

public interface GroupWeekRowHeaderCellRenderer {

	public abstract Component getGroupWeekRowHeaderCellRendererComponent(
			JGroupWeekRowHeader groupWeekRowHeader, boolean isSelected,
			boolean hasFocus, int group, int row);

}
