package com.synaptix.swing;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.border.LineBorder;

public class LineRoundBorder extends LineBorder {

	private static final long serialVersionUID = -5916122648649390767L;

	public LineRoundBorder(Color lineColor) {
		this(lineColor, 1);
	}

	public LineRoundBorder(Color lineColor, int thickness) {
		super(lineColor, thickness);
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		Graphics2D g2 = (Graphics2D) g.create();

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g2.setColor(lineColor);
		int round = thickness * 4;

		g2.setStroke(new BasicStroke(thickness));

		g2.drawLine(x + round + thickness, y, x + width - 1 - thickness, y);
		g2.drawLine(x + width - 1, y, x + width - 1, y + height - 1 - round
				- thickness);
		g2.drawLine(x + width - 1 - round - thickness, y + height - 1, x
				+ thickness, y + height - 1);
		g2.drawLine(x, y + height - 1, x, y + round + thickness);

		g2.drawArc(x, y, round * 2, round * 2, 90, 90);
		g2.drawArc(x + width - 1 - round * 2, y + height - 1 - round * 2,
				round * 2, round * 2, 270, 90);

		g2.dispose();
	}
}