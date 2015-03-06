package com.synaptix.swing.groupweek;

import java.awt.Component;

import com.synaptix.swing.JGroupWeek;

public interface GroupWeekCellRenderer {

	public abstract Component getGroupWeekCellRendererComponent(
			JGroupWeek groupWeek, Object value, boolean isSelected,
			boolean hasFocus, int group, int row, int day);

}
