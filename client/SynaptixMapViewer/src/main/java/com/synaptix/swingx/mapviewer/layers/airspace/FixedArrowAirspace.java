package com.synaptix.swingx.mapviewer.layers.airspace;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.cache.Cacheable;
import org.jdesktop.swingx.mapviewer.util.CacheKey;

import com.synaptix.swingx.mapviewer.util.Vec2;

public class FixedArrowAirspace extends AbstractShapeAirspace {

	private GeoPosition locationStart;

	private GeoPosition locationEnd;

	private double maxScreenSize;

	private boolean arrowStart;

	private boolean arrowEnd;

	private boolean fineStart = true;

	private boolean fineEnd = false;

	private Path2D path2d = new Path2D.Double();

	public FixedArrowAirspace(GeoPosition locationStart, boolean arrowStart,
			GeoPosition locationEnd, boolean arrowEnd, double maxScreenSize) {
		super();

		this.locationStart = locationStart;
		this.locationEnd = locationEnd;
		this.arrowStart = arrowStart;
		this.arrowEnd = arrowEnd;
		this.maxScreenSize = maxScreenSize;
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

	public boolean isArrowStart() {
		return arrowStart;
	}

	public void setArrowStart(boolean arrowStart) {
		this.arrowStart = arrowStart;
	}

	public boolean isArrowEnd() {
		return arrowEnd;
	}

	public void setArrowEnd(boolean arrowEnd) {
		this.arrowEnd = arrowEnd;
	}

	public void setFineStart(boolean fineStart) {
		this.fineStart = fineStart;
	}

	public boolean isFineStart() {
		return fineStart;
	}

	public void setFineEnd(boolean fineEnd) {
		this.fineEnd = fineEnd;
	}

	public boolean isFineEnd() {
		return fineEnd;
	}

	private MyGeometryCachable getGeometryCachable(DrawContext dc) {
		CacheKey key = new CacheKey(FixedArrowAirspace.class,
				"fixedArrowAirspace", locationStart, arrowStart, fineStart,
				locationEnd, arrowEnd, fineStart);
		MyGeometryCachable wc = (MyGeometryCachable) dc.getMemoryCache()
				.getObject(key);
		if (wc == null) {
			Point2D p1 = dc.getTileFactory().geoToPixel(locationStart,
					dc.getTileFactory().getInfo().getMinimumZoomLevel());
			Vec2 vecA = new Vec2(p1.getX(), p1.getY());
			Point2D p2 = dc.getTileFactory().geoToPixel(locationEnd,
					dc.getTileFactory().getInfo().getMinimumZoomLevel());
			Vec2 vecB = new Vec2(p2.getX(), p2.getY());
			Vec2 normalAB = vecA.subtract2(vecB).normalize2();
			Vec2 perpendicular = normalAB.perpendicular();
			wc = new MyGeometryCachable(vecA, vecB, normalAB, perpendicular,
					vecA.distanceTo2(vecB) / 4.0);
			dc.getMemoryCache().add(key, wc);
		}
		return wc;
	}

	private double getScale(DrawContext dc) {
		int zoomMin = dc.getTileFactory().getInfo().getMinimumZoomLevel();
		int zoom = (int) Math.floor(dc.getRealZoom());
		return (1 + 1 - (dc.getRealZoom() - zoom))
				/ Math.pow(2, zoom - zoomMin + 1);
	}

	@Override
	public Shape createShape(DrawContext dc) {
		MyGeometryCachable gc = getGeometryCachable(dc);
		double scale = getScale(dc);

		double midDistanceAB = gc.distanceAB * scale;
		double size = maxScreenSize >= midDistanceAB ? midDistanceAB
				: maxScreenSize;

		double xl = gc.perpendicular.x * size;
		double yl = gc.perpendicular.y * size;

		double xp = xl / 2.0;
		double yp = yl / 2.0;

		double xA = gc.vecA.x * scale;
		double yA = gc.vecA.y * scale;

		double xB = gc.vecB.x * scale;
		double yB = gc.vecB.y * scale;

		double xmA = xA - gc.normalAB.x * size;
		double ymA = yA - gc.normalAB.y * size;

		double xmB = xB + gc.normalAB.x * size;
		double ymB = yB + gc.normalAB.y * size;

		path2d.reset();

		path2d.moveTo(xA, yA);

		if (!arrowStart && !fineStart) {
			path2d.lineTo(xA - xp, yA - yp);
		}

		if (arrowStart) {
			path2d.lineTo(xmA - xl, ymA - yl);
			path2d.lineTo(xmA - xp, ymA - yp);
		}

		if (arrowEnd) {
			path2d.lineTo(xmB - xp, ymB - yp);
			path2d.lineTo(xmB - xl, ymB - yl);
		}

		if (arrowEnd || fineEnd) {
			path2d.lineTo(xB, yB);
		} else {
			path2d.lineTo(xB - xp, yB - yp);
			path2d.lineTo(xB + xp, yB + yp);
		}

		if (arrowEnd) {
			path2d.lineTo(xmB + xl, ymB + yl);
			path2d.lineTo(xmB + xp, ymB + yp);
		}

		if (arrowStart) {
			path2d.lineTo(xmA + xp, ymA + yp);
			path2d.lineTo(xmA + xl, ymA + yl);
		}

		if (!arrowStart && !fineStart) {
			path2d.lineTo(xA + xp, yA + yp);
		}

		path2d.lineTo(xA, yA);

		return path2d;
	}

	@Override
	protected Shape getSelectedShape(DrawContext dc, Shape shape) {
		MyGeometryCachable gc = getGeometryCachable(dc);
		double scale = getScale(dc);

		double midDistanceAB = gc.distanceAB * scale + 5;
		double size = maxScreenSize + 5 >= midDistanceAB ? midDistanceAB
				: maxScreenSize + 5;
		double xA = gc.vecA.x * scale;
		double yA = gc.vecA.y * scale;

		double xB = gc.vecB.x * scale;
		double yB = gc.vecB.y * scale;

		double xl = gc.perpendicular.x * size;
		double yl = gc.perpendicular.y * size;

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

		public final Vec2 normalAB;

		public final Vec2 perpendicular;

		public final double distanceAB;

		public MyGeometryCachable(Vec2 vecA, Vec2 vecB, Vec2 normalAB,
				Vec2 perpendicular, double distanceAB) {
			super();

			this.vecA = vecA;
			this.vecB = vecB;
			this.normalAB = normalAB;
			this.perpendicular = perpendicular;
			this.distanceAB = distanceAB;
		}

		public long getSizeInBytes() {
			return 4 * 8 * 2;
		}
	}
}
