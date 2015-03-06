package com.synaptix.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class JTitleBar extends JLabel {

	private static final long serialVersionUID = -8025490734257864161L;

	private static final Font defaultFont = new Font("Tahoma", Font.BOLD, 10); //$NON-NLS-1$

	private Color contourColor = new Color(192, 192, 192);

	private Color fondColor = new Color(224, 224, 224);

	private Color textColor = new Color(0, 0, 0);

	public JTitleBar(String text) {
		super(text);

		this.setFont(defaultFont);
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
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
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		int w = this.getWidth();
		int h = this.getHeight();

		int left = 2;
		int top = 2;

		paintBackground(g2, left, top, w - left * 2, h - top * 2);

		g2.dispose();

		super.paintComponent(g);
	}

	private void paintBackground(Graphics g, int x, int y, int w, int h) {
		Graphics2D g2 = (Graphics2D) g.create();

		int aw = 5;
		int ah = 5;

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
}
