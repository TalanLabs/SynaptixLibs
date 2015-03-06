package com.synaptix.swing.tooltip;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

public class JDefaultToolTip extends JComponent {

	private static final long serialVersionUID = 4846077969395265027L;

	public enum PositionArrow {
		TOP, LEFT, RIGHT, BOTTOM
	}

	public enum AlignArrow {
		TOP, LEFT, CENTER, RIGHT, BOTTOM
	}

	private int arrow;

	private PositionArrow positionArrow;

	private AlignArrow alignArrow;

	private Insets insetsToolTip;

	private String text;

	private Color borderColor;

	private Color bottomColor;

	private Color topColor;

	public JDefaultToolTip() {
		this(null);
	}

	public JDefaultToolTip(String text) {
		super();

		this.text = text;

		this.setOpaque(false);

		this.setForeground(new Color(0x5c5c5c));
		this.borderColor = new Color(0xe5e5e5);
		this.bottomColor = new Color(0xf6f6f6);
		this.topColor = new Color(0xf9f9f9);
		this.setFont(new Font("Dialog", Font.BOLD, 11));
		this.insetsToolTip = new Insets(5, 5, 5, 5);
		this.arrow = 5;
		this.positionArrow = PositionArrow.TOP;
		this.alignArrow = AlignArrow.LEFT;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;

		repaint();
	}

	public Color getBottomColor() {
		return bottomColor;
	}

	public void setBottomColor(Color bottomColor) {
		this.bottomColor = bottomColor;

		repaint();
	}

	public Color getTopColor() {
		return topColor;
	}

	public void setTopColor(Color topColor) {
		this.topColor = topColor;

		repaint();
	}

	public PositionArrow getPositionArrow() {
		return positionArrow;
	}

	public void setPositionArrow(PositionArrow positionArrow) {
		this.positionArrow = positionArrow;

		revalidate();
		repaint();
	}

	public AlignArrow getAlignArrow() {
		return alignArrow;
	}

	public void setAlignArrow(AlignArrow alignArrow) {
		this.alignArrow = alignArrow;

		revalidate();
		repaint();
	}

	public Insets getInsetsToolTip() {
		return insetsToolTip;
	}

	public void setInsetsToolTip(Insets insetsToolTip) {
		this.insetsToolTip = insetsToolTip;

		revalidate();
		repaint();
	}

	public void setText(String text) {
		String oldText = this.text;
		this.text = text;

		firePropertyChange("text", oldText, text);

		revalidate();
		repaint();
	}

	public String getText() {
		return text;
	}

	@Override
	public Dimension getPreferredSize() {
		Font font = this.getFont();
		Rectangle2D rect = font.getStringBounds(text, new FontRenderContext(new AffineTransform(), true, false));

		int w = 0;
		int h = 0;
		int minW = 0;
		int minH = 0;
		switch (positionArrow) {
		case TOP:
		case BOTTOM:
			minW = insetsToolTip.left + arrow * 2 + insetsToolTip.right;
			w = insetsToolTip.left + (int) rect.getWidth() + insetsToolTip.right;
			minH = arrow + insetsToolTip.top + insetsToolTip.bottom;
			h = minH + (int) rect.getHeight();
			break;
		case LEFT:
		case RIGHT:
			minW = arrow + insetsToolTip.left + insetsToolTip.right;
			w = minW + (int) rect.getWidth();
			minH = insetsToolTip.top + arrow * 2 + insetsToolTip.bottom;
			h = insetsToolTip.top + (int) rect.getHeight() + insetsToolTip.bottom;
			break;
		}
		return new Dimension(Math.max(minW, w), Math.max(minH, h));
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		switch (positionArrow) {
		case TOP:
		case BOTTOM:
			paintTopBottomArrow(g2);
			break;
		case LEFT:
		case RIGHT:
			paintLeftRightArrow(g2);
			break;
		}

		g2.dispose();
	}

	protected void paintBackground(Graphics2D g2, int x, int y, int w, int h) {
		g2.setColor(borderColor);
		g2.fillRect(x, y, w, h);

		g2.setColor(bottomColor);
		g2.fillRect(x, y, w, h - 1);

		g2.setColor(topColor);
		g2.fillRect(x, y, w - 3, (h - 1) / 2);
		g2.fillRect(x, y, 3, h - 1);
	}

	protected void paintText(Graphics2D g2, int x, int y, int w, int h) {
		g2.setFont(this.getFont());
		LineMetrics lm = g2.getFontMetrics().getLineMetrics(text, g2);

		g2.setColor(this.getForeground());
		g2.drawString(text, x + insetsToolTip.left, y + h - lm.getDescent() - insetsToolTip.bottom);
	}

	protected void paintTopBottomArrow(Graphics2D g2) {
		int w = this.getWidth();
		int h = this.getHeight();

		int y1 = positionArrow.equals(PositionArrow.TOP) ? arrow : 0;

		paintBackground(g2, 0, y1, w, h - arrow);

		int arrowX = 0;
		switch (alignArrow) {
		case LEFT:
		case TOP:
			arrowX = arrow;
			break;
		case RIGHT:
		case BOTTOM:
			arrowX = w - arrow * 3 - 1;
			break;
		case CENTER:
			arrowX = w / 2 - arrow;
			break;
		}
		g2.setColor(positionArrow.equals(PositionArrow.TOP) ? topColor : bottomColor);
		g2.fill(positionArrow.equals(PositionArrow.TOP) ? createTopArrowShape(arrowX, 0) : createBottomArrowShape(arrowX, h - arrow - 1));

		paintText(g2, 0, y1, w, h - arrow);
	}

	protected Shape createTopArrowShape(int x, int y) {
		Path2D path = new Path2D.Float();
		path.moveTo(x, y + arrow);
		path.lineTo(x + arrow, y);
		path.lineTo(x + arrow * 2, y + arrow);
		path.lineTo(x, y + arrow);
		return path;
	}

	protected Shape createBottomArrowShape(int x, int y) {
		Path2D path = new Path2D.Float();
		path.moveTo(x, y);
		path.lineTo(x + arrow, y + arrow);
		path.lineTo(x + arrow * 2, y);
		path.lineTo(x, y);
		return path;
	}

	protected void paintLeftRightArrow(Graphics2D g2) {
		int w = this.getWidth();
		int h = this.getHeight();

		int x1 = positionArrow.equals(PositionArrow.LEFT) ? arrow : 0;

		paintBackground(g2, x1, 0, w - arrow, h);

		int arrowY = 0;
		switch (alignArrow) {
		case LEFT:
		case TOP:
			arrowY = arrow;
			break;
		case RIGHT:
		case BOTTOM:
			arrowY = h - arrow * 3 - 1;
			break;
		case CENTER:
			arrowY = h / 2 - arrow;
			break;
		}
		g2.setColor(positionArrow.equals(PositionArrow.LEFT) ? topColor : bottomColor);
		g2.fill(positionArrow.equals(PositionArrow.LEFT) ? createLeftArrowShape(0, arrowY) : createRightArrowShape(w - arrow - 1, arrowY));

		paintText(g2, x1, 0, w - arrow, h);
	}

	protected Shape createLeftArrowShape(int x, int y) {
		Path2D path = new Path2D.Float();
		path.moveTo(x + arrow, y);
		path.lineTo(x, y + arrow);
		path.lineTo(x + arrow, y + arrow * 2);
		path.lineTo(x + arrow, y);
		return path;
	}

	protected Shape createRightArrowShape(int x, int y) {
		Path2D path = new Path2D.Float();
		path.moveTo(x, y);
		path.lineTo(x + arrow, y + arrow);
		path.lineTo(x, y + arrow * 2);
		path.lineTo(x, y);
		return path;
	}
}
