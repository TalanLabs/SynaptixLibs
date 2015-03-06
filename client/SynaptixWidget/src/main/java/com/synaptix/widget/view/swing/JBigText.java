package com.synaptix.widget.view.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import org.apache.commons.lang3.StringUtils;

import com.synaptix.swing.utils.GraphicsHelper;

public class JBigText extends JComponent {

	private static final long serialVersionUID = 1818058615495741905L;

	private double angle = Math.toRadians(-22.5);

	private double scale = 0.75;

	private String text;

	public JBigText() {
		this("");
	}

	public JBigText(String text) {
		super();

		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		String oldValue = this.text;
		this.text = text;
		firePropertyChange("text", oldValue, text);
	}

	/**
	 * Radians
	 * 
	 * @return
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * 
	 * @param angle
	 *            radians
	 */
	public void setAngle(double angle) {
		double oldValue = this.angle;
		this.angle = angle;
		firePropertyChange("angle", oldValue, angle);
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		double oldValue = this.scale;
		this.scale = scale;
		firePropertyChange("scale", oldValue, scale);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Insets insets = this.getInsets();
		int width = this.getWidth() - (insets.left + insets.right);
		int height = this.getHeight() - (insets.top + insets.bottom);

		if (StringUtils.isNotBlank(text)) {
			Graphics2D g2 = (Graphics2D) g.create();
			GraphicsHelper.activeAntiAliasing(g2);

			GlyphVector gv = this.getFont().createGlyphVector(g2.getFontRenderContext(), text);
			Shape shape = gv.getOutline();

			Rectangle2D rect = shape.getBounds2D();

			AffineTransform t1 = new AffineTransform();
			t1.translate(rect.getCenterX(), rect.getCenterY());
			t1.rotate(angle);
			t1.translate(-rect.getCenterX(), -rect.getCenterY());
			shape = t1.createTransformedShape(shape);

			rect = shape.getBounds2D();

			double widthComponent = width * scale;
			double heightComponent = height * scale;
			double diffComponent = widthComponent / heightComponent;

			double diffText = rect.getWidth() / rect.getHeight();

			double diff = diffText / diffComponent;

			double w = widthComponent;
			double h = heightComponent;
			if (diff >= 1.0) {
				h = h / diff;
			} else {
				w = w * diff;
			}

			double sx = w / rect.getWidth();
			double sy = h / rect.getHeight();

			AffineTransform t2 = new AffineTransform();
			t2.translate((this.getWidth() - w) / 2.0, (this.getHeight() - h) / 2.0);
			t2.scale(sx, sy);
			t2.translate(-rect.getX(), -rect.getY());

			shape = t2.createTransformedShape(shape);
			paintShape(g2, shape);

			g2.dispose();
		}
	}

	protected void paintShape(Graphics2D g2, Shape s) {
		g2.setColor(this.getForeground());
		g2.fill(s);
	}
}