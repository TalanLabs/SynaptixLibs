package com.synaptix.swingx.mapviewer.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class TextRenderer {

	private Font font;

	private FontRenderContext fontRenderContext;

	private Color color;

	public TextRenderer(Font font) {
		super();

		this.font = font;
		this.fontRenderContext = new FontRenderContext(new AffineTransform(),
				true, false);
	}

	public Font getFont() {
		return font;
	}

	public Rectangle2D getBounds(String text) {
		return font.getStringBounds(text, fontRenderContext);
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void draw(Graphics2D g, String text, int x, int y) {
		g.setColor(color);
		g.setFont(font);
		g.drawString(text, x, y);
	}

	public LineMetrics getLineMetrics(String text) {
		return font.getLineMetrics(text, fontRenderContext);
	}
}
