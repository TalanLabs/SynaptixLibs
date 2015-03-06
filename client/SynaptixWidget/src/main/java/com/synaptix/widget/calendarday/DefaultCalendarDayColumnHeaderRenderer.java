package com.synaptix.widget.calendarday;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;

public class DefaultCalendarDayColumnHeaderRenderer extends JLabel implements CalendarDayColumnHeaderRenderer {

	private static final long serialVersionUID = -8778628984142379086L;

	public DefaultCalendarDayColumnHeaderRenderer() {
		super();

		this.setBackground(Color.GRAY);
		this.setForeground(Color.WHITE);
		this.setHorizontalAlignment(JLabel.CENTER);
		this.setOpaque(true);
		this.setFont(this.getFont().deriveFont(Font.BOLD));
	}

	@Override
	public Component getCalendarDayColumnHeaderRendererComponent(JCalendarDay calendarDay, String name, int column) {
		this.setText(name);
		return this;
	}

}
