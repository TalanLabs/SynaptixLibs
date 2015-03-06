package com.synaptix.swing.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class AscendingIcon extends NullIcon {
	
	public void paintIcon(Component pComponent, Graphics g, int pX, int pY) {
		Graphics2D g2 = (Graphics2D) g;

		int[] xx = { pX, pX + 3, pX + 6 };
		int[] yy = { pY + 6, pY, pY + 6 };

		Color initialColor = g2.getColor();
		float[] initial = Color.RGBtoHSB(initialColor.getRed(), initialColor
				.getGreen(), initialColor.getBlue(), null);

		boolean need = initial[2] < .5f;
		initial[2] = invertAsNeed(initial[2], need);

		Color line = Color.getHSBColor(initial[0], initial[1], invertAsNeed(
				initial[2] * .7f, need));
		Color fill = Color.getHSBColor(initial[0], initial[1], invertAsNeed(
				initial[2] * .35f, need));
		g2.setColor(fill);
		g2.fillPolygon(xx, yy, 3);
		g2.setColor(line);
		g2.drawPolygon(xx, yy, 3);

		String text = String.valueOf(numberOrder);
		Font font = g2.getFont();
		Rectangle2D rect = font
				.getStringBounds(text, g2.getFontRenderContext());
		g2.setColor(numberOrderColor);
		g2.drawString(text, pX + 7, (int) (pY + rect.getHeight() / 2));
	}
}
