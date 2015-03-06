package com.synaptix.swingx.mapviewer.layers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.layers.AbstractLayer;

/**
 * Calque qui affiche la longitude et latitude du centre et le zoom
 * 
 * @author Gaby
 * 
 */
public class InfoLayer extends AbstractLayer {

	private Color backgroundColor = new Color(0f, 0f, 0f, 0.8f);

	private Color textColor = Color.white;

	private Font textFont = new Font("Arial", Font.PLAIN, 15);

	private int height = 20;

	@Override
	protected void doPick(DrawContext dc, Point point) {
	}

	@Override
	protected void doPaint(Graphics2D g, DrawContext dc) {
		int y = dc.getDrawableHeight() - height;

		Graphics2D g2 = (Graphics2D) g.create(0, y, dc.getDrawableWidth(),
				height);

		g2.setColor(backgroundColor);
		g2.fillRect(0, 0, dc.getDrawableWidth(), height);

		g2.setFont(textFont);
		g2.setColor(textColor);

		GeoPosition gp = dc.getCenterPosition();

		String las = String.format("Lat %7.4f\u00B0", gp.getLatitude());
		String los = String.format("Lon %7.4f\u00B0", gp.getLongitude());
		String zs = String.format("Zoom %2.1f", dc.getRealZoom());

		StringBuilder sb = new StringBuilder();
		sb.append(las).append(" ").append(los);
		sb.append(" ").append(zs);

		LineMetrics lm = textFont.getLineMetrics(sb.toString(),
				g2.getFontRenderContext());
		Rectangle2D rect = textFont.getStringBounds(sb.toString(),
				g2.getFontRenderContext());

		g2.drawString(sb.toString(),
				(int) (dc.getDrawableWidth() - rect.getWidth()) / 2,
				lm.getAscent());

		g2.dispose();
	}
}
