package com.synaptix.swing.groupweek;

import java.awt.Component;

import javax.swing.JLabel;

import com.synaptix.swing.JGroupWeek;

public class DefaultGroupWeekCellRenderer extends JLabel implements
		GroupWeekCellRenderer {

	private static final long serialVersionUID = 4204466717741279579L;

	public DefaultGroupWeekCellRenderer() {
		super();

		this.setOpaque(true);

		this.setHorizontalAlignment(JLabel.CENTER);
		this.setHorizontalTextPosition(JLabel.LEFT);
	}

	public Component getGroupWeekCellRendererComponent(JGroupWeek groupWeek,
			Object value, boolean isSelected, boolean hasFocus, int group,
			int row, int day) {
		if (isSelected) {
			this.setBackground(groupWeek.getSelectionBackground());
			this.setForeground(groupWeek.getSelectionForeground());
		} else {
			this.setBackground(groupWeek.getBackground());
			this.setForeground(groupWeek.getForeground());
		}
		if (value != null) {
			this.setText(value.toString());
		} else {
			this.setText(null);
		}
		return this;
	}
}