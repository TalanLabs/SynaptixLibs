package com.synaptix.swingx.mapviewer.layers.render;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.GeoPosition;

public class GlobeAnnotation extends AbstractAnnotation {

	protected GeoPosition position;

	public GlobeAnnotation(String text, GeoPosition position) {
		super();

		this.text = text;
		this.position = position;

		this.getAnnotationAttributes().setDrawOffset(new Point(0, 0));
	}

	public GeoPosition getPosition() {
		return position;
	}

	public void setPosition(GeoPosition position) {
		this.position = position;
	}

	@Override
	protected void doRenderNow(Graphics2D g, DrawContext dc) {
		Point2D screenPoint = dc.convertGeoPositionToPoint(position);
		Rectangle rect = getBounds(dc);
		Dimension size = this.getPreferredSize(dc);

		if (rect.x < dc.getDrawableWidth() && rect.x + rect.width > 0 && rect.y < dc.getDrawableHeight() && rect.y + rect.height > 0) {
			drawTopLevelAnnotation(g, dc, (int) screenPoint.getX(), (int) screenPoint.getY(), size.width, size.height, 1, 1);
		}
	}

	@Override
	protected Rectangle computeBounds(DrawContext dc) {
		Point2D screenPoint = dc.convertGeoPositionToPoint(position);

		Dimension size = this.getPreferredSize(dc);
		double finalScale = this.computeScale(dc);
		Point offset = this.getAnnotationAttributes().getDrawOffset();

		double offsetX = offset.x * finalScale;
		double offsetY = offset.y * finalScale;
		double width = size.width * finalScale;
		double height = size.height * finalScale;
		double x = screenPoint.getX() - width / 2 + offsetX;
		double y = screenPoint.getY() - height / 2 + offsetY;

		Rectangle frameRect = new Rectangle((int) x, (int) y, (int) width, (int) height);

		// Include reference point in bounds
		return this.computeBoundingRectangle(frameRect, (int) screenPoint.getX(), (int) screenPoint.getY());
	}

}
