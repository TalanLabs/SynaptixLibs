package com.synaptix.swingx.mapviewer.layers.airspace;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.cache.Cacheable;
import org.jdesktop.swingx.mapviewer.util.CacheKey;

import com.synaptix.swingx.mapviewer.util.Vec2;

public class FixedArcArrowAirspace extends AbstractShapeAirspace {

	private GeoPosition locationStart;

	private GeoPosition locationEnd;

	private double maxScreenSize;

	private double angle;

	private Path2D path2d = new Path2D.Double();

	public FixedArcArrowAirspace(GeoPosition locationStart, GeoPosition locationEnd, double maxScreenSize, double angle) {
		super();

		this.locationStart = locationStart;
		this.locationEnd = locationEnd;
		this.maxScreenSize = maxScreenSize;
		this.angle = angle;
	}

	public GeoPosition getLocationStart() {
		return locationStart;
	}

	public void setLocationStart(GeoPosition locationStart) {
		this.locationStart = locationStart;
	}

	public GeoPosition getLocationEnd() {
		return locationEnd;
	}

	public void setLocationEnd(GeoPosition locationEnd) {
		this.locationEnd = locationEnd;
	}

	public double getMaxScreenSize() {
		return maxScreenSize;
	}

	public void setMaxScreenSize(double maxScreenSize) {
		this.maxScreenSize = maxScreenSize;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	private MyGeometryCachable getGeometryCachable(DrawContext dc) {
		CacheKey key = new CacheKey(FixedArcArrowAirspace.class, "fixedArcArrowAirspace", locationStart, locationEnd, angle);
		MyGeometryCachable wc = (MyGeometryCachable) dc.getMemoryCache().getObject(key);
		if (wc == null) {
			Point2D p1 = dc.getTileFactory().geoToPixel(locationStart, dc.getTileFactory().getInfo().getMinimumZoomLevel());
			Vec2 vecA = new Vec2(p1.getX(), p1.getY());
			Point2D p2 = dc.getTileFactory().geoToPixel(locationEnd, dc.getTileFactory().getInfo().getMinimumZoomLevel());
			Vec2 vecB = new Vec2(p2.getX(), p2.getY());
			Vec2 normalAB = vecA.subtract2(vecB).normalize2();
			Vec2 perpendicularAB = normalAB.perpendicular();
			double distance = vecA.distanceTo2(vecB);
			Vec2 vecC = vecB.add2(vecA).multiply2(0.5);
			double oppose = Math.tan(angle) * distance / 2.0;
			Vec2 vecD = vecC.add2(perpendicularAB.multiply2(oppose));
			Vec2 normalAD = vecA.subtract2(vecD).normalize2();
			Vec2 perpendicularAD = normalAD.perpendicular();
			Vec2 normalBD = vecB.subtract2(vecD).normalize2();
			Vec2 perpendicularBD = normalBD.perpendicular();

			wc = new MyGeometryCachable(vecA, vecB, perpendicularAB, vecD, distance / 4.0, perpendicularAD, normalBD, perpendicularBD);
			dc.getMemoryCache().add(key, wc);
		}
		return wc;
	}

	private double getScale(DrawContext dc) {
		int zoomMin = dc.getTileFactory().getInfo().getMinimumZoomLevel();
		int zoom = (int) Math.floor(dc.getRealZoom());
		return (1 + 1 - (dc.getRealZoom() - zoom)) / Math.pow(2, zoom - zoomMin + 1);
	}

	@Override
	public Shape createShape(DrawContext dc) {
		MyGeometryCachable gc = getGeometryCachable(dc);
		double scale = getScale(dc);

		Vec2 vecA = new Vec2(gc.vecA.x * scale, gc.vecA.y * scale);
		Vec2 vecB = new Vec2(gc.vecB.x * scale, gc.vecB.y * scale);

		// Distance AB
		double midDistanceAB = gc.quartDistanceAB * scale;
		double tickness = (maxScreenSize >= midDistanceAB ? midDistanceAB : maxScreenSize) / 2.0;

		Vec2 vecC = vecB.add2(vecA).multiply2(0.5);
		double oppose = 20;
		Vec2 vecD = vecC.add2(gc.perpendicularAB.multiply2(oppose));
		Vec2 normalAD = vecA.subtract2(vecD).normalize2();
		Vec2 perpendicularAD = normalAD.perpendicular().multiply2(tickness);
		Vec2 normalBD = vecB.subtract2(vecD).normalize2();
		Vec2 perpendicularBD = normalBD.perpendicular().multiply2(tickness);

		// Point au dessus de AB
		// Vec2 vecD = new Vec2(gc.vecD.x * scale, gc.vecD.y * scale);

		// Vec2 perpendicularAD = gc.perpendicularAD.multiply2(tickness);
		Vec2 vecA1 = vecA.subtract2(perpendicularAD);
		Vec2 vecA2 = vecA.add2(perpendicularAD);

		// Vec2 perpendicularBD = gc.perpendicularBD.multiply2(tickness);
		Vec2 vecB0 = vecB.subtract2(normalBD.multiply2(tickness * 2));
		Vec2 vecB1 = vecB0.add2(perpendicularBD);
		Vec2 vecB11 = vecB1.add2(perpendicularBD);
		Vec2 vecB2 = vecB0.subtract2(perpendicularBD);
		Vec2 vecB22 = vecB2.subtract2(perpendicularBD);

		Vec2 perpD = gc.perpendicularAB.multiply2(tickness);
		Vec2 vecD1 = vecD.subtract2(perpD);
		Vec2 vecD2 = vecD.add2(perpD);

		path2d.reset();

		path2d.moveTo(vecA.x, vecA.y);
		path2d.lineTo(vecA1.x, vecA1.y);

		// Curve
		path2d.curveTo(vecD1.x, vecD1.y, vecD1.x, vecD1.y, vecB1.x, vecB1.y);

		// Arrow B
		path2d.lineTo(vecB11.x, vecB11.y);
		path2d.lineTo(vecB.x, vecB.y);
		path2d.lineTo(vecB22.x, vecB22.y);
		path2d.lineTo(vecB2.x, vecB2.y);

		// Curve
		path2d.curveTo(vecD2.x, vecD2.y, vecD2.x, vecD2.y, vecA2.x, vecA2.y);
		// path2d.curveTo(vecD2.x, vecD2.y, vecD2.x, vecD2.y, vecA.x, vecA.y);
		// p.lineTo(vecD2.x, vecD2.y);
		// p.lineTo(vecA2.x, vecA2.y);

		path2d.lineTo(vecA.x, vecA.y);

		return path2d;
	}

	@Override
	protected Shape getSelectedShape(DrawContext dc, Shape shape) {
		MyGeometryCachable gc = getGeometryCachable(dc);
		double scale = getScale(dc);

		double midDistanceAB = gc.quartDistanceAB * scale + 5;
		double size = maxScreenSize + 5 >= midDistanceAB ? midDistanceAB : maxScreenSize + 5;
		double xA = gc.vecA.x * scale;
		double yA = gc.vecA.y * scale;

		double xB = gc.vecB.x * scale;
		double yB = gc.vecB.y * scale;

		double xl = gc.perpendicularAB.x * size;
		double yl = gc.perpendicularAB.y * size;

		path2d.reset();

		path2d.moveTo(xA - xl, yA - yl);
		path2d.lineTo(xB - xl, yB - yl);
		path2d.lineTo(xB + xl, yB + yl);
		path2d.lineTo(xA + xl, yA + yl);
		path2d.lineTo(xA - xl, yA - yl);

		return path2d;
	}

	private static final class MyGeometryCachable implements Cacheable {

		public final Vec2 vecA;

		public final Vec2 vecB;

		public final Vec2 perpendicularAB;

		public final Vec2 vecD;

		public final double quartDistanceAB;

		public final Vec2 perpendicularAD;

		public final Vec2 normalBD;

		public final Vec2 perpendicularBD;

		public MyGeometryCachable(Vec2 vecA, Vec2 vecB, Vec2 perpendicularAB, Vec2 vecD, double quartDistanceAB, Vec2 perpendicularAD, Vec2 normalBD, Vec2 perpendicularBD) {
			super();

			this.vecA = vecA;
			this.vecB = vecB;
			this.perpendicularAB = perpendicularAB;
			this.vecD = vecD;
			this.quartDistanceAB = quartDistanceAB;
			this.perpendicularAD = perpendicularAD;
			this.normalBD = normalBD;
			this.perpendicularBD = perpendicularBD;
		}

		public long getSizeInBytes() {
			return 5 * 8 * 2 + 8 + 8;
		}
	}
}