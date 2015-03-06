package com.synaptix.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;

public class JTauxLabel extends JLabel {

	private static final long serialVersionUID = 7388584552867300961L;

	public enum Type {
		Progressif, Plein, Inverse
	};

	private boolean showBackground;

	private Number taux;

	private Type type;

	private Color color;

	public JTauxLabel() {
		this(Type.Progressif);
	}

	public JTauxLabel(Type type) {
		super("", JLabel.CENTER);
		this.setOpaque(false);
		this.setFont(new Font("arial", Font.BOLD, 12));

		this.type = type;

		this.showBackground = true;
	}

	public void setTaux(Number taux) {
		Number oldValue = getTaux();
		this.taux = taux;
		if (taux != null) {
			StringBuilder sb = new StringBuilder();
			sb.append((int) (taux.doubleValue() * 100));
			sb.append(" %");
			this.setText(sb.toString());

			double i = taux.doubleValue();
			if (i <= 0) {
				color = Color.RED;
			} else if (i > 1) {
				color = Color.GREEN;
			} else {

				int r = interpole(Color.RED.getRed(), Color.GREEN.getRed(), i);
				int v = interpole(Color.RED.getGreen(), Color.GREEN.getGreen(),
						i);
				int b = interpole(Color.RED.getBlue(), Color.GREEN.getBlue(), i);
				color = new Color(r, v, b);
			}
		} else {
			this.setText(null);
			color = Color.RED;
		}

		firePropertyChange("taux", oldValue, taux);

		repaint();
	}

	public Number getTaux() {
		return taux;
	}

	public void setShowBackground(boolean showBackground) {
		boolean oldValue = isShowBackground();
		this.showBackground = showBackground;

		firePropertyChange("showBackground", oldValue, showBackground);

		repaint();
	}

	public boolean isShowBackground() {
		return showBackground;
	}

	private int interpole(int c1, int c2, double p) {
		return (int) ((double) (c2 - c1) * p + (double) c1);
	}

	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		int width = this.getWidth();
		int height = this.getHeight();

		if (showBackground) {
			if (type == Type.Progressif) {
				if (taux != null && taux.doubleValue() >= 0
						&& taux.doubleValue() <= 1) {
					GradientPaint gradientPaint = new GradientPaint(0, 0,
							Color.GREEN, width, 0, Color.RED);
					g2.setPaint(gradientPaint);

					int w = (int) (taux.doubleValue() * width);
					g2.fillRect(0, 0, w, height);
				} else {
					g2.setColor(Color.RED);
					g2.fillRect(0, 0, width, height);
				}
			} else if (type == Type.Plein) {
				if (taux != null) {
					g2.setColor(color);
				} else {
					g2.setColor(Color.RED);
				}
				g2.fillRect(0, 0, width, height);
			} else if (type == Type.Inverse) {
				if (taux != null) {
					if (taux.doubleValue() < 0) {
						g2.setColor(Color.RED);
					} else if (taux.doubleValue() >= 1.0) {
						g2.setColor(Color.GREEN);
					} else {
						GradientPaint gradientPaint = new GradientPaint(0, 0,
								Color.RED, width, 0, Color.GREEN);
						g2.setPaint(gradientPaint);
					}
					int w = (int) (taux.doubleValue() * width);
					g2.fillRect(0, 0, w, height);
				} else {
					g2.setColor(Color.RED);
					g2.fillRect(0, 0, width, height);
				}
			}
		} else {
			g2.setColor(this.getBackground());
			g2.fillRect(0, 0, width, height);
		}

		super.paintComponent(g);
	}
}
