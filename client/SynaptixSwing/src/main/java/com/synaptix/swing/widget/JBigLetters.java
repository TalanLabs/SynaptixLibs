package com.synaptix.swing.widget;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

public class JBigLetters extends JComponent {

	private static final long serialVersionUID = -5133102565941295233L;

	private static boolean round = true;

	private static final Font defaultFont = new Font("Tahoma", Font.BOLD, 10); //$NON-NLS-1$

	private Color contourColor = new Color(192, 192, 192);

	private Color fondColor = new Color(224, 224, 224);

	private Color textColor = new Color(0, 0, 0);

	private Image logoImage;

	private String text;

	private String firstLetter;

	private String bottomText;

	public JBigLetters() {
		super();

		this.setFont(defaultFont);
	}

	public static void setRound(boolean round) {
		JBigLetters.round = round;
	}

	public static boolean isRound() {
		return round;
	}

	public void setText(String text) {
		setText(text, true);
	}

	public void setText(String text, boolean firstOnly) {
		this.text = text;

		if (text != null && !text.isEmpty()) {
			if (!firstOnly) {
				firstLetter = text.toUpperCase();
			} else {
				firstLetter = text.substring(0, 1).toUpperCase();

				if (text.length() > 1) {
					bottomText = text.substring(1).toUpperCase();
				} else {
					bottomText = null;
				}
			}
		} else {
			firstLetter = null;
			bottomText = null;
		}

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

	public Image getLogoImage() {
		return logoImage;
	}

	public void setLogoImage(Image logoImage) {
		this.logoImage = logoImage;

		repaint();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		int w = this.getWidth();
		int h = this.getHeight();

		int left = w / 15;
		int top = h / 15;

		int x1 = left * 2;
		int y1 = top * 2;
		int w1 = w - (4 * left);
		int h1 = h - (4 * top);

		int milieu = h1 * 3 / 4;

		paintBackground(g2, left, top, w - left * 2, h - top * 2);

		if (text != null && !text.isEmpty()) {
			if (bottomText != null) {
				paintFirstLetter(g2, x1, y1, w1, milieu);
				paintBottomText(g2, x1, y1 + milieu, w1, h1 / 4);
			} else {
				paintFirstLetter(g2, x1, y1, w1, h1);
			}
		}

		if (logoImage != null) {
			g2.drawImage(logoImage, w - left * 3, 0, left * 3, top * 3, null);
		}

		g2.dispose();
	}

	private void paintBackground(Graphics g, int x, int y, int w, int h) {
		Graphics2D g2 = (Graphics2D) g.create();

		int aw = w / 10;
		int ah = h / 10;

		if (this.isOpaque()) {
			g2.setColor(this.getBackground());
			g2.fillRect(0, 0, w, h);
		}

		g2.setColor(fondColor);
		if (round) {
			g2.fillRoundRect(x, y, w - 1, h - 1, aw, ah);
		} else {
			g2.fillRect(x, y, w - 1, h - 1);
		}

		g2.setColor(contourColor);
		if (round) {
			g2.drawRoundRect(x, y, w - 1, h - 1, aw, ah);
		} else {
			g2.drawRect(x, y, w - 1, h - 1);
		}

		g2.dispose();
	}

	private void paintFirstLetter(Graphics2D g2, int x, int y, int w, int h) {
		GlyphVector firstLetterGV = this.getFont().createGlyphVector(g2.getFontRenderContext(), firstLetter);
		Shape firstLetterShape = firstLetterGV.getOutline();

		Rectangle2D rect = firstLetterShape.getBounds2D();
		double sx = (w - 1) / rect.getWidth();
		double sy = (h - 1) / rect.getHeight();

		AffineTransform t = new AffineTransform();
		t.translate(x, y);
		t.scale(sx, sy);
		t.translate(-rect.getX(), -rect.getY());

		Shape s = t.createTransformedShape(firstLetterShape);

		g2.setColor(textColor);
		g2.fill(s);
	}

	private void paintBottomText(Graphics2D g2, int x, int y, int w, int h) {
		GlyphVector otherGV = this.getFont().createGlyphVector(g2.getFontRenderContext(), bottomText);
		Shape bottomTextShape = otherGV.getOutline();

		Rectangle2D rect = bottomTextShape.getBounds2D();
		double sx = (w - 1) / rect.getWidth();
		double sy = (h - 1) / rect.getHeight();

		AffineTransform t = new AffineTransform();
		t.translate(x, y);
		t.scale(sx, sy);
		t.translate(-rect.getX(), -rect.getY());

		g2.setColor(textColor);
		g2.fill(t.createTransformedShape(bottomTextShape));
	}
}