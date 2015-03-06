package com.synaptix.swing.groupweek;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

public class DefaultGroupWeekRowHeaderCellRenderer extends JLabel implements
		GroupWeekRowHeaderCellRenderer {

	private static final long serialVersionUID = 4204466717741279579L;

	public DefaultGroupWeekRowHeaderCellRenderer() {
		super();

		this.setOpaque(true);
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		this.setHorizontalAlignment(JLabel.CENTER);
		this.setHorizontalTextPosition(JLabel.LEFT);
	}

	public Component getGroupWeekRowHeaderCellRendererComponent(
			JGroupWeekRowHeader groupWeekRowHeader, boolean isSelected,
			boolean hasFocus, int group, int row) {
		if (isSelected) {
			this.setBackground(groupWeekRowHeader.getGroupWeek()
					.getSelectionBackground());
			this.setForeground(groupWeekRowHeader.getGroupWeek()
					.getSelectionForeground());
		} else {
			this.setBackground(groupWeekRowHeader.getGroupWeek()
					.getBackground());
			this.setForeground(groupWeekRowHeader.getGroupWeek()
					.getForeground());
		}
		if (row >= 0) {
			this.setText(String.valueOf(row + 1));
		} else if (group >= 0) {
			this.setText(String.valueOf(group + 1));
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
}