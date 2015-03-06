package com.synaptix.swing.widget;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

/**
 * Permet d'ecrire un text verticalement
 * 
 * @author PGAE02801
 * 
 */
public class JInformationPanel extends JComponent {

	private static final long serialVersionUID = -1559921166912693044L;

	private static final Font smallFont = new Font("Tahoma", Font.PLAIN, 10); //$NON-NLS-1$

	private Color contourColor = new Color(192, 192, 192);

	private Color fondColor = new Color(224, 224, 224);

	private Color textColor = new Color(0, 0, 0);

	private String text;

	public JInformationPanel() {
		super();

		this.text = null;

		this.setFont(smallFont);
	}

	public void setText(String text) {
		this.text = text;

		revalidate();
		repaint();
	}

	public String getText() {
		return text;
	}

	public Color getContourColor() {
		return contourColor;
	}

	public void setContourColor(Color contourColor) {
		this.contourColor = contourColor;

		repaint();
	}

	public Color getFondColor() {
		return fondColor;
	}

	public void setFondColor(Color fondColor) {
		this.fondColor = fondColor;

		repaint();
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;

		repaint();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (isVisible() && text != null && !text.isEmpty()) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);

			int w = this.getWidth();
			int h = this.getHeight();

			paintBackground(g2, 0, 0, w, h);
			paintBottomText(g2, 0, 0, w, h);

			g2.dispose();
		}
	}

	private void paintBackground(Graphics2D g2, int x, int y, int w, int h) {
		g2 = (Graphics2D) g2.create();

		int aw = w / 10;
		int ah = h / 10;

		if (this.isOpaque()) {
			g2.setColor(this.getBackground());
			g2.fillRect(0, 0, w, h);
		}

		g2.setColor(fondColor);
		g2.fillRoundRect(x, y, w - 1, h - 1, aw, ah);

		g2.setColor(contourColor);
		g2.drawRoundRect(x, y, w - 1, h - 1, aw, ah);

		g2.dispose();
	}

	private void paintBottomText(Graphics2D g2, int x, int y, int w, int h) {
		g2 = (Graphics2D) g2.create();

		GlyphVector otherGV = this.getFont().createGlyphVector(
				g2.getFontRenderContext(), text);
		Shape bottomTextShape = otherGV.getOutline();

		Rectangle2D rect = bottomTextShape.getBounds2D();

		AffineTransform t = new AffineTransform();
		t.translate(x, y + (h + rect.getWidth()) / 2);
		t.rotate(-Math.PI / 2);
		t.translate(-rect.getX(), -rect.getY());

		g2.setColor(textColor);
		Shape s = t.createTransformedShape(bottomTextShape);
		g2.fill(s);

		g2.dispose();
	}
}
