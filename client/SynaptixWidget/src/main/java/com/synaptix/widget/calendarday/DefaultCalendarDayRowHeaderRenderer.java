package com.synaptix.widget.calendarday;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;

public class DefaultCalendarDayRowHeaderRenderer extends JLabel implements CalendarDayRowHeaderRenderer {

	private static final long serialVersionUID = -8778628984142379086L;

	public DefaultCalendarDayRowHeaderRenderer() {
		super();

		this.setBackground(Color.GRAY);
		this.setForeground(Color.WHITE);
		this.setHorizontalAlignment(JLabel.CENTER);
		this.setOpaque(true);
		this.setFont(this.getFont().deriveFont(Font.BOLD));
	}

	@Override
	public Component getCalendarDayRowHeaderRendererComponent(JCalendarDay calendarDay, int row) {
		this.setText(String.valueOf(row + 1));
		return this;
	}

}
