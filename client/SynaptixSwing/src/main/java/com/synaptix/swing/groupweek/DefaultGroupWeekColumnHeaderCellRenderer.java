package com.synaptix.swing.groupweek;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.DateFormatSymbols;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

public class DefaultGroupWeekColumnHeaderCellRenderer extends JLabel implements
		GroupWeekColumnHeaderCellRenderer {

	private static final long serialVersionUID = 4204466717741279579L;

	private String[] listDayNames;

	public DefaultGroupWeekColumnHeaderCellRenderer() {
		super();

		this.setOpaque(true);
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		this.setHorizontalAlignment(JLabel.CENTER);
		this.setHorizontalTextPosition(JLabel.LEFT);

		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
		listDayNames = dateFormatSymbols.getWeekdays();
	}

	public Component getGroupWeekColumnHeaderCellRendererComponent(
			JGroupWeekColumnHeader groupWeekHeader, boolean isSelected,
			boolean hasFocus, int day) {
		if (isSelected) {
			this.setBackground(groupWeekHeader.getGroupWeek()
					.getSelectionBackground());
			this.setForeground(groupWeekHeader.getGroupWeek()
					.getSelectionForeground());
		} else {
			this.setBackground(groupWeekHeader.getGroupWeek().getBackground());
			this.setForeground(groupWeekHeader.getGroupWeek().getForeground());
		}
		int calendar = calendarAtDay(day);
		if (calendar != 0) {
			this.setText(listDayNames[calendar]);
		} else {
			this.setText("");
		}
		return this;
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		int w = this.getWidth();
		int h = this.getHeight();

		Color c1 = this.getBackground();
		Color c2 = c1.brighter().brighter();

		GradientPaint gp = new GradientPaint(0, 0, c1, 0, h * 3 / 4, c2);
		g2.setPaint(gp);
		g2.fillRect(0, 0, w, h * 3 / 4);

		gp = new GradientPaint(0, h * 3 / 4, c2, 0, h, c1);
		g2.setPaint(gp);
		g2.fillRect(0, h * 3 / 4, w, h);

		super.paintComponent(g);
	}

	/**
	 * Donne le calendar selon un day
	 * 
	 * 0 => Calendar.MONDAY, 1 => Calendar.TUESDAY, etc
	 * 
	 * @param day
	 * @return
	 */
	public static final int calendarAtDay(int day) {
		int res = 0;
		switch (day) {
		case 0:
			res = Calendar.MONDAY;
			break;
		case 1:
			res = Calendar.TUESDAY;
			break;
		case 2:
			res = Calendar.WEDNESDAY;
			break;
		case 3:
			res = Calendar.THURSDAY;
			break;
		case 4:
			res = Calendar.FRIDAY;
			break;
		case 5:
			res = Calendar.SATURDAY;
			break;
		case 6:
			res = Calendar.SUNDAY;
			break;
		}
		return res;
	}

}