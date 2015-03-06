package com.synaptix.swingx.mapviewer.layers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jdesktop.swingx.mapviewer.AVKey;
import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.layers.AbstractLayer;

/**
 * Calque pour afficher une echelle
 * 
 * @author Gaby
 * 
 */
public class ScalableLayer extends AbstractLayer implements AVKey {

	public final static String UNIT_METRIC = "org.jdesktop.swingx.mapviewer.layers.ScalableLayer.Metric";

	public final static String UNIT_IMPERIAL = "org.jdesktop.swingx.mapviewer.layers.ScalableLayer.Imperial";

	private Dimension size = new Dimension(150, 10);

	private Color color = Color.white;

	private int borderWidth = 20;

	private String unit = UNIT_METRIC;

	private String position = AVKey.SOUTHEAST;

	private Point2D locationCenter = null;

	private Point2D locationOffset = null;

	private Font font = Font.decode("Arial-PLAIN-12");

	public ScalableLayer() {
		this(AVKey.SOUTHEAST);
	}

	public ScalableLayer(String position) {
		super();

		this.setAntialiasing(true);
		this.setCacheable(false);

		this.setPickEnabled(false);

		this.position = position;
	}

	public Point2D getLocationCenter() {
		return locationCenter;
	}

	public void setLocationCenter(Point2D locationCenter) {
		this.locationCenter = locationCenter;
	}

	public Point2D getLocationOffset() {
		return locationOffset;
	}

	public void setLocationOffset(Point2D locationOffset) {
		this.locationOffset = locationOffset;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
	}

	public void setSize(Dimension size) {
		this.size = size;
	}

	public Dimension getSize() {
		return size;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	private final float[] compArray = new float[4];

	// Compute background color for best contrast
	private Color getBackgroundColor(Color color) {
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), compArray);
		if (compArray[2] > 0.5)
			return new Color(0, 0, 0, 0.7f);
		else
			return new Color(1, 1, 1, 0.7f);
	}

	@Override
	protected void doPick(DrawContext dc, Point point) {
	}

	@Override
	protected void doPaint(Graphics2D g, DrawContext dc) {
		Graphics2D g2 = (Graphics2D) g.create();

		double width = this.size.width;
		double height = this.size.height;

		double scale = this.computeScale(dc.getViewportBounds());
		double pixelSize = dc.computePixelSize();

		Double scaleSize = pixelSize * width * scale; // meter
		String unitLabel = "m";
		if (this.unit.equals(UNIT_METRIC)) {
			if (scaleSize > 10000) {
				scaleSize /= 1000;
				unitLabel = "Km";
			}
		} else if (this.unit.equals(UNIT_IMPERIAL)) {
			scaleSize *= 3.280839895; // feet
			unitLabel = "ft";
			if (scaleSize > 5280) {
				scaleSize /= 5280;
				unitLabel = "mile(s)";
			}
		}

		Point2D locationSW = this.computeLocation(dc.getViewportBounds(), scale);
		g2.translate(locationSW.getX(), locationSW.getY());

		int pot = (int) Math.floor(Math.log10(scaleSize));
		if (!Double.isNaN(pot)) {
			int digit = Integer.parseInt(String.format("%.0f", scaleSize).substring(0, 1));
			double divSize = digit * Math.pow(10, pot);
			if (digit >= 5)
				divSize = 5 * Math.pow(10, pot);
			else if (digit >= 2)
				divSize = 2 * Math.pow(10, pot);
			double divWidth = width * divSize / scaleSize;

			Color backColor = getBackgroundColor(color);

			g2.setColor(color);
			g2.translate((width - divWidth) / 2, 0);
			this.drawScale(g2, divWidth, height);

			g2.setColor(backColor);
			g2.translate(1d / scale, 1d / scale);
			this.drawScale(g2, divWidth, height);
			g2.translate(-(width - divWidth) / 2 + 1, -1);

			String label = String.format("%.0f ", divSize) + unitLabel;

			Rectangle2D rect = font.getStringBounds(label, g2.getFontRenderContext());
			double d = (width - rect.getWidth()) / 2;
			g2.setColor(color);
			g2.drawString(label, (int) d, -(int) height);
			g2.setColor(backColor);
			g2.drawString(label, (int) d + 1, -(int) height + 1);
		}

		g2.dispose();
	}

	private void drawScale(Graphics2D g, double width, double height) {
		g.drawLine(0, 0, 0, -(int) height - 1);
		g.drawLine(0, 0, (int) width - 1, 0);
		g.drawLine((int) width - 1, 0, (int) width - 1, -(int) height - 1);

		g.drawLine((int) width / 2, 0, (int) width / 2, -(int) height / 2);
	}

	private Point2D computeLocation(java.awt.Rectangle viewport, double scale) {
		double scaledWidth = scale * this.size.width;
		double scaledHeight = scale * this.size.height;

		double x;
		double y;

		if (this.locationCenter != null) {
			x = this.locationCenter.getX() - scaledWidth / 2;
			y = this.locationCenter.getY() - scaledHeight / 2;
		} else if (this.position.equals(AVKey.NORTHEAST)) {
			x = viewport.getWidth() - scaledWidth - this.borderWidth;
			y = 0d + this.borderWidth;
		} else if (this.position.equals(AVKey.SOUTHEAST)) {
			x = viewport.getWidth() - scaledWidth - this.borderWidth;
			y = viewport.getHeight() - scaledHeight - this.borderWidth;
		} else if (this.position.equals(AVKey.NORTHWEST)) {
			x = 0d + this.borderWidth;
			y = 0d + this.borderWidth;
		} else if (this.position.equals(AVKey.SOUTHWEST)) {
			x = 0d + this.borderWidth;
			y = viewport.getHeight() - scaledHeight - this.borderWidth;
		} else // use North East
		{
			x = viewport.getWidth() - scaledWidth / 2 - this.borderWidth;
			y = 0d + this.borderWidth;
		}

		if (this.locationOffset != null) {
			x += this.locationOffset.getX();
			y += this.locationOffset.getY();
		}

		return new Point2D.Double(x, y);
	}

	private double computeScale(java.awt.Rectangle viewport) {
		return 1d;
	}
}
