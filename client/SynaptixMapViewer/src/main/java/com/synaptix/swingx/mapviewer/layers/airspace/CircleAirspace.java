package com.synaptix.swingx.mapviewer.layers.airspace;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.cache.Cacheable;
import org.jdesktop.swingx.mapviewer.util.CacheKey;

import com.synaptix.swingx.mapviewer.util.Vec2;

public class CircleAirspace extends AbstractShapeAirspace {

	private GeoPosition locationCenter;

	private double distance;

	private Ellipse2D ellipse2D = new Ellipse2D.Double();

	public CircleAirspace(GeoPosition locationCenter, double distance) {
		super();

		this.locationCenter = locationCenter;
		this.distance = distance;
	}

	public GeoPosition getLocationCenter() {
		return locationCenter;
	}

	public void setLocationCenter(GeoPosition locationCenter) {
		this.locationCenter = locationCenter;
	}

	/**
	 * Get a distance (Meter)
	 * 
	 * @return
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Set a distance (Meter)
	 * 
	 * @param distance
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

	private MyGeometryCachable getGeometryCachable(DrawContext dc) {
		CacheKey key = new CacheKey(CircleAirspace.class, "circleAirspace", locationCenter, distance);
		MyGeometryCachable wc = (MyGeometryCachable) dc.getMemoryCache().getObject(key);
		if (wc == null) {
			Point2D p1 = dc.getTileFactory().geoToPixel(locationCenter, dc.getTileFactory().getInfo().getMinimumZoomLevel());
			Vec2 vecA = new Vec2(p1.getX(), p1.getY());
			Point2D p2 = dc.getTileFactory().geoToPixel(locationCenter.add(distance, distance), dc.getTileFactory().getInfo().getMinimumZoomLevel());
			Vec2 vecB = new Vec2(p2.getX(), p2.getY());
			wc = new MyGeometryCachable(vecA, vecB);
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

		double xA = gc.vecA.x * scale;
		double yA = gc.vecA.y * scale;

		double xB = gc.vecB.x * scale;
		double yB = gc.vecB.y * scale;

		ellipse2D.setFrameFromCenter(xA, yA, xB, yB);

		return ellipse2D;
	}

	private static final class MyGeometryCachable implements Cacheable {

		public final Vec2 vecA;

		public final Vec2 vecB;

		public MyGeometryCachable(Vec2 vecA, Vec2 vecB) {
			super();

			this.vecA = vecA;
			this.vecB = vecB;
		}

		public long getSizeInBytes() {
			return 2 * 8 * 2;
		}
	}
}
