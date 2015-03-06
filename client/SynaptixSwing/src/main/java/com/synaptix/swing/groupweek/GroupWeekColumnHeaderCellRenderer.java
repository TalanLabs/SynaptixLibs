package com.synaptix.swing.groupweek;

import java.awt.Component;

public interface GroupWeekColumnHeaderCellRenderer {

	public abstract Component getGroupWeekColumnHeaderCellRendererComponent(
			JGroupWeekColumnHeader groupWeekColumnHeader, boolean isSelected,
			boolean hasFocus, int day);

}
