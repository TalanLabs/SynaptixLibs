package com.synaptix.widget.component.view.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import com.synaptix.swing.utils.Utils;
import com.synaptix.widget.util.StaticWidgetHelper;

public final class NoResultPanel extends JComponent {

	private static final long serialVersionUID = 7226743752829045958L;

	private static final Color backgroundColor = new Color(180, 180, 180, 60);

	private static final Font defaultTextFont = new Font("Arial", Font.BOLD, 15);

	private boolean ghost;

	private String text = StaticWidgetHelper.getSynaptixWidgetConstantsBundle().launchSearch();

	public NoResultPanel() {
		super();

		this.ghost = true;

		setEnabled(false);

		initComponents();
	}

	private void initComponents() {
	}

	public void setText(String text) {
		this.text = text;
	}

	public void showGhost() {
		this.ghost = true;

		repaint();
	}

	public void hideGhost() {
		this.ghost = false;

		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (ghost) {
			Graphics2D g2 = (Graphics2D) g.create();

			int w = this.getWidth();
			int h = this.getHeight();

			g2.setColor(backgroundColor);
			g2.fillRect(0, 0, w, h);

			g2.setColor(getForeground());
			paintCenterString(g2, text, defaultTextFont, 0, 0, w, h);

			g2.dispose();
		}
	}

	private void paintCenterString(Graphics g, String text, Font font, int x, int y, int width, int height) {
		if (text != null && !text.isEmpty()) {
			Graphics2D g2 = (Graphics2D) g.create(x, y, width, height);
			g2.setFont(font);
			String t = Utils.getClippedText(text, g2.getFontMetrics(), width);
			Rectangle2D rect2 = font.getStringBounds(t, g2.getFontRenderContext());
			LineMetrics lm = font.getLineMetrics(t, g2.getFontRenderContext());
			final int x2 = (int) (width - rect2.getWidth()) / 2;
			final float y2 = height / 2 + lm.getAscent() / 2;
			g2.drawString(t, x2, y2);
			g2.dispose();
		}
	}
}
