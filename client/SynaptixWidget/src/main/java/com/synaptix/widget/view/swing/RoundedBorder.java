package com.synaptix.widget.view.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import javax.swing.border.AbstractBorder;

import com.synaptix.swing.utils.GraphicsHelper;

public class RoundedBorder extends AbstractBorder {

	private static final long serialVersionUID = -6579952369775335251L;

	public enum Mode {

		NONE, LINE, ROUNDED

	}

	private Mode headerMode;

	private Mode footerMode;

	private int thickness = 5;

	private Insets insets;

	private float divide = 0.5f;

	public RoundedBorder() {
		this(Mode.ROUNDED, Mode.ROUNDED);
	}

	public RoundedBorder(Mode headerMode, Mode footerMode) {
		this(headerMode, footerMode, 5);
	}

	public RoundedBorder(Mode headerMode, Mode footerMode, int thickness) {
		super();

		this.headerMode = headerMode;
		this.footerMode = footerMode;
		this.thickness = thickness;
	}

	public int getThickness() {
		return thickness;
	}

	public void setThickness(int thickness) {
		this.thickness = thickness;
		this.insets = null;
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}

	@Override
	public Insets getBorderInsets(Component c) {
		if (insets == null) {
			int t = (int) (thickness * divide);
			insets = new Insets(t, t, t, t);
		}
		return insets;
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		int t = (int) (thickness * divide);
		insets.bottom = insets.left = insets.right = insets.top = t;
		return insets;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2 = (Graphics2D) g.create();
		GraphicsHelper.activeAntiAliasing(g2);

		Path2D.Float path2d = new Path2D.Float();
		switch (headerMode) {
		case NONE:
			path2d.moveTo(x, y - 1);
			path2d.lineTo(x + width - 1, y - 1);
			break;
		case LINE:
			path2d.moveTo(x, y);
			path2d.lineTo(x + width - 1, y);
			break;
		case ROUNDED:
			path2d.moveTo(x, y + thickness);
			path2d.curveTo(x, y, x, y, x + thickness, y);
			path2d.lineTo(x + width - thickness - 1, y);
			path2d.curveTo(x + width - 1, y, x + width - 1, y, x + width - 1, y + thickness);
			break;
		}

		switch (footerMode) {
		case NONE:
			path2d.lineTo(x + width - 1, y + height);
			path2d.lineTo(x, y + height);
			break;
		case LINE:
			path2d.lineTo(x + width - 1, y + height - 1);
			path2d.lineTo(x, y + height - 1);
			break;
		case ROUNDED:
			path2d.lineTo(x + width - 1, y + height - thickness - 1);
			path2d.curveTo(x + width - 1, y + height - 1, x + width - 1, y + height - 1, x + width - thickness - 1, y + height - 1);
			path2d.lineTo(x + thickness, y + height - 1);
			path2d.curveTo(x, y + height - 1, x, y + height - 1, x, y + height - thickness - 1);
			break;
		}

		switch (headerMode) {
		case NONE:
			path2d.lineTo(x, y - 1);
			break;
		case LINE:
			path2d.lineTo(x, y);
			break;
		case ROUNDED:
			path2d.lineTo(x, y + thickness);
			break;
		}

		int t = (int) (thickness * divide);

		Area area = new Area(path2d);
		area.subtract(new Area(new Rectangle2D.Float(x + t, y + t, width - t * 2, height - t * 2)));

		g2.setColor(c.getBackground());
		// g2.fill(area);

		g2.setColor(Color.LIGHT_GRAY);
		g2.draw(path2d);

		g2.dispose();
	}
}
