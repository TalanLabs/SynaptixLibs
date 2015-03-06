package com.synaptix.swingx.mapviewer.layers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.layers.AbstractLayer;

/**
 * Calque qui affiche une croix au centre
 * 
 * @author Gaby
 * 
 */
public class CrosshairLayer extends AbstractLayer {

	private Color borderColor = Color.black;

	private Color crossColor = Color.white;

	private int sizeCross = 20;

	@Override
	protected void doPick(DrawContext dc, Point point) {
	}

	protected void doPaint(Graphics2D g, DrawContext dc) {
		Graphics2D g2 = (Graphics2D) g.create();
		int dw = dc.getDrawableWidth() / 2;
		int dh = dc.getDrawableHeight() / 2;
		int sc = sizeCross / 5;

		g2.setColor(crossColor);
		g2.drawLine(dw, dh - sizeCross, dw, dh - sc);
		g2.drawLine(dw, dh + sizeCross, dw, dh + sc);
		g2.drawLine(dw - sizeCross, dh, dw - sc, dh);
		g2.drawLine(dw + sizeCross, dh, dw + sc, dh);

		g2.setColor(borderColor);
		g2.drawLine(dw + 1, dh - sizeCross, dw + 1, dh - sc);
		g2.drawLine(dw + 1, dh + sizeCross, dw + 1, dh + sc);
		g2.drawLine(dw - sizeCross, dh + 1, dw - sc, dh + 1);
		g2.drawLine(dw + sizeCross, dh + 1, dw + sc, dh + 1);

		g2.dispose();
	}
}
