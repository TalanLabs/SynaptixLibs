package com.synaptix.widget.grid;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import javax.swing.JComponent;

import com.synaptix.swing.utils.GraphicsHelper;

public class JGridCellMini extends JComponent implements PropertyChangeListener {

	private static final long serialVersionUID = 740699395931318124L;

	private Color pageFillColor = new Color(255, 255, 255, 255);

	private Color pageStrokeColor = new Color(178, 178, 178, 255);

	private Color rectFillColor = new Color(128, 128, 128, 255);

	private Insets pageBorderInsets = new Insets(3, 3, 3, 3);

	private int numGridX = -1;

	private int numGridY = -1;

	private Collection<Rectangle> rects;

	private boolean showPage = true;

	public JGridCellMini() {
		super();

		this.addPropertyChangeListener(this);
	}

	public int getNumGridX() {
		return numGridX;
	}

	public void setNumGridX(int numGridX) {
		int oldValue = this.numGridX;
		this.numGridX = numGridX;
		firePropertyChange("numGridX", oldValue, numGridX);
	}

	public int getNumGridY() {
		return numGridY;
	}

	public void setNumGridY(int numGridY) {
		int oldValue = this.numGridY;
		this.numGridY = numGridY;
		firePropertyChange("numGridY", oldValue, numGridY);
	}

	public boolean isShowPage() {
		return showPage;
	}

	public void setShowPage(boolean showPage) {
		boolean oldValue = this.showPage;
		this.showPage = showPage;
		firePropertyChange("showPage", oldValue, showPage);
	}

	public Color getPageFillColor() {
		return pageFillColor;
	}

	public void setPageFillColor(Color pageFillColor) {
		Color oldValue = this.pageFillColor;
		this.pageFillColor = pageFillColor;
		firePropertyChange("pageFillColor", oldValue, pageFillColor);
	}

	public Color getPageStrokeColor() {
		return pageStrokeColor;
	}

	public void setPageStrokeColor(Color pageStrokeColor) {
		Color oldValue = this.pageFillColor;
		this.pageStrokeColor = pageStrokeColor;
		firePropertyChange("pageStrokeColor", oldValue, pageStrokeColor);
	}

	public Insets getPageBorderInsets() {
		return pageBorderInsets;
	}

	public void setPageBorderInsets(Insets pageBorderInsets) {
		Insets oldValue = this.pageBorderInsets;
		this.pageBorderInsets = pageBorderInsets;
		firePropertyChange("pageBorderInsets", oldValue, pageBorderInsets);
	}

	public Color getRectFillColor() {
		return rectFillColor;
	}

	public void setRectFillColor(Color rectFillColor) {
		Color oldValue = this.rectFillColor;
		this.rectFillColor = rectFillColor;
		firePropertyChange("rectFillColor", oldValue, rectFillColor);
	}

	public Collection<Rectangle> getRects() {
		return rects;
	}

	public void setRects(Collection<Rectangle> rects) {
		Collection<Rectangle> oldValue = this.rects;
		this.rects = rects;
		firePropertyChange("rects", oldValue, rects);
	}

	public void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		resizeAndRepaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (numGridX != -1 && numGridY != -1) {
			int w = this.getWidth();
			int h = this.getHeight();
			Insets insets = this.getInsets();

			float sw = (w - (insets.left + pageBorderInsets.left + pageBorderInsets.right + insets.right)) / (float) numGridX;
			float sh = (h - (insets.top + pageBorderInsets.top + pageBorderInsets.bottom + insets.bottom)) / (float) numGridY;

			float size = Math.min(sw, sh);
			float x = (w - (numGridX * size + insets.left + pageBorderInsets.left + pageBorderInsets.right + insets.right)) / 2;
			float y = (h - (numGridY * size + insets.top + pageBorderInsets.top + pageBorderInsets.bottom + insets.bottom)) / 2;

			x += insets.left;
			y += insets.top;
			float pw = numGridX * size + pageBorderInsets.left + pageBorderInsets.right;
			float ph = numGridY * size + pageBorderInsets.top + pageBorderInsets.bottom;

			if (showPage) {
				paintPage(g, x, y, pw, ph);
			}

			if (this.rects != null && !this.rects.isEmpty()) {
				x += pageBorderInsets.left;
				y += pageBorderInsets.top;
				paintRects(g, x, y, size);
			}
		}
	}

	private Rectangle2D.Float tempRect = new Rectangle2D.Float();

	protected void paintPage(Graphics g, float px, float py, float pw, float ph) {
		Graphics2D g2 = (Graphics2D) g.create();
		GraphicsHelper.activeAntiAliasing(g2);

		tempRect.setRect(px, py, pw, ph);

		if (getPageFillColor() != null) {
			g2.setColor(getPageFillColor());
			g2.fill(tempRect);
		}

		if (getPageStrokeColor() != null) {
			g2.setColor(getPageStrokeColor());
			g2.draw(tempRect);
		}

		g2.dispose();
	}

	protected Color getRectFillColor(Rectangle rect) {
		return rectFillColor;
	}

	protected void paintRects(Graphics g, float x, float y, float size) {
		Graphics2D g2 = (Graphics2D) g.create();
		GraphicsHelper.activeAntiAliasing(g2);

		for (Rectangle rect : rects) {
			float rw = rect.width * size - (rect.x + rect.width == numGridX ? 0 : 1);
			float rh = rect.height * size - (rect.y + rect.height == numGridY ? 0 : 1);
			paintRect(g2, rect, x + rect.x * size, y + rect.y * size, rw, rh);
		}

		g2.dispose();
	}

	protected void paintRect(Graphics2D g2, Rectangle rect, float x, float y, float w, float h) {
		g2.setColor(getRectFillColor(rect));
		tempRect.setRect(x, y, w, h);
		g2.fill(tempRect);
	}
}
