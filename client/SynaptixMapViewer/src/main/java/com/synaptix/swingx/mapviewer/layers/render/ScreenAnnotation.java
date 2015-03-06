package com.synaptix.swingx.mapviewer.layers.render;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import org.jdesktop.swingx.mapviewer.AVKey;
import org.jdesktop.swingx.mapviewer.DrawContext;

public class ScreenAnnotation extends AbstractAnnotation {

	protected Point screenPoint;

	public ScreenAnnotation(String text, Point point) {
		super();

		this.text = text;
		this.screenPoint = point;

		this.getAnnotationAttributes().setDrawOffset(new Point(0, 0));
	}

	public Point getScreenPoint() {
		return screenPoint;
	}

	public void setScreenPoint(Point screenPoint) {
		this.screenPoint = screenPoint;
	}

	@Override
	protected Rectangle computeBounds(DrawContext dc) {
		Dimension size = this.getPreferredSize(dc);
		double finalScale = this.computeScale(dc);
		Point offset = getAnnotationAttributes().getDrawOffset();

		double offsetX = offset.x * finalScale;
		double offsetY = offset.y * finalScale;
		double width = size.width * finalScale;
		double height = size.height * finalScale;

		double dx = 0;
		if (AVKey.ALIGN_CENTER.equals(getAnnotationAttributes()
				.getFrameHorizontalAlign())) {
			dx = -width / 2;
		} else if (AVKey.ALIGN_RIGHT.equals(getAnnotationAttributes()
				.getFrameHorizontalAlign())) {
			dx = -width;
		}
		double dy = 0;
		if (AVKey.ALIGN_CENTER.equals(getAnnotationAttributes()
				.getFrameVerticalAlign())) {
			dy = -height / 2;
		} else if (AVKey.ALIGN_BOTTOM.equals(getAnnotationAttributes()
				.getFrameVerticalAlign())) {
			dy = -height;
		}

		Point sp = this.getScreenPoint();
		double x = sp.x + dx + offsetX;
		double y = sp.y + dy + offsetY;

		Rectangle frameRect = new Rectangle((int) x, (int) y, (int) width,
				(int) height);

		// Include reference point in bounds
		return this.computeBoundingRectangle(frameRect, sp.x, sp.y);
	}

	@Override
	protected void doRenderNow(Graphics2D g, DrawContext dc) {
		Dimension size = getPreferredSize(dc);
		drawTopLevelAnnotation(g, dc, screenPoint.x, screenPoint.y, size.width,
				size.height, 1, 1);
	}
}
