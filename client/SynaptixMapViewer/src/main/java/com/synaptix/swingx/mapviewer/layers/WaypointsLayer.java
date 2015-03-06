package com.synaptix.swingx.mapviewer.layers;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.PickedObject;
import org.jdesktop.swingx.mapviewer.cache.Cacheable;
import org.jdesktop.swingx.mapviewer.layers.AbstractLayer;
import org.jdesktop.swingx.mapviewer.util.CacheKey;

import com.synaptix.swingx.mapviewer.layers.waypoint.DefaultWaypointRenderer;
import com.synaptix.swingx.mapviewer.layers.waypoint.Waypoint;
import com.synaptix.swingx.mapviewer.layers.waypoint.WaypointRenderer;

public class WaypointsLayer extends AbstractLayer {

	private WaypointRenderer renderer = new DefaultWaypointRenderer();

	private List<? extends Waypoint> waypoints;

	public WaypointsLayer() {
		super();

		this.setAntialiasing(true);
		this.setCacheable(false);

		this.setPickEnabled(true);

		waypoints = new ArrayList<Waypoint>();
	}

	public void setRenderer(WaypointRenderer r) {
		this.renderer = r;
	}

	public List<? extends Waypoint> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(List<? extends Waypoint> waypoints) {
		this.waypoints = waypoints;
	}

	private MyWaypointsCachable getWaypointsCachable(DrawContext dc) {
		CacheKey key = new CacheKey(WaypointsLayer.class, "waypoints", waypoints);
		MyWaypointsCachable wc = (MyWaypointsCachable) dc.getMemoryCache().getObject(key);
		if (wc == null) {
			List<Point2D> pList = new ArrayList<Point2D>();
			for (Waypoint w : waypoints) {
				pList.add(dc.getTileFactory().geoToPixel(w.getPosition(), dc.getTileFactory().getInfo().getMinimumZoomLevel()));
			}
			wc = new MyWaypointsCachable(pList);
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
	protected void doPick(DrawContext dc, Point p) {
		if (renderer == null || waypoints == null || waypoints.isEmpty()) {
			return;
		}

		MyWaypointsCachable wc = getWaypointsCachable(dc);
		double scale = getScale(dc);

		Point2D point = new Point2D.Double();
		Waypoint waypoint = null;
		Point2D point2 = null;
		Rectangle rect = null;

		double x = dc.getViewportBounds().getX() + p.getX();
		double y = dc.getViewportBounds().getY() + p.getY();

		for (int i = 0; i < waypoints.size(); i++) {
			waypoint = waypoints.get(i);
			point2 = wc.pointList.get(i);
			rect = renderer.getBoundingBox(dc, this, waypoint);

			point.setLocation(point2.getX() * scale, point2.getY() * scale);

			if (rect.contains(x - point.getX(), y - point.getY())) {
				PickedObject po = new PickedObject(p, waypoint);
				po.setLayer(this);
				dc.addPickedObject(po);
			}
		}
	}

	@Override
	protected void doPaint(Graphics2D g, DrawContext dc) {
		if (renderer == null || waypoints == null || waypoints.isEmpty()) {
			return;
		}

		MyWaypointsCachable wc = getWaypointsCachable(dc);
		double scale = getScale(dc);

		Point2D point = new Point2D.Double();
		Waypoint waypoint = null;
		Point2D point2 = null;
		for (int i = 0; i < waypoints.size(); i++) {
			waypoint = waypoints.get(i);
			point2 = wc.pointList.get(i);

			point.setLocation(point2.getX() * scale, point2.getY() * scale);

			if (dc.getViewportBounds().contains(point)) {
				int x = (int) (point.getX() - dc.getViewportBounds().getX());
				int y = (int) (point.getY() - dc.getViewportBounds().getY());
				g.translate(x, y);
				paintWaypoint(g, dc, waypoint);
				g.translate(-x, -y);

				if (dc.isPickingMode() && dc.getPickPoint().x >= x - 10 && dc.getPickPoint().x <= x + 10 && dc.getPickPoint().y >= y - 10 && dc.getPickPoint().y <= y + 10) {
					PickedObject po = new PickedObject(dc.getPickPoint(), waypoint);
					po.setLayer(this);
					dc.addPickedObject(po);
				}
			}
		}
	}

	protected void paintWaypoint(Graphics2D g, DrawContext dc, Waypoint w) {
		renderer.paintWaypoint(g, dc, this, w);
	}

	private static final class MyWaypointsCachable implements Cacheable {

		private final List<Point2D> pointList;

		public MyWaypointsCachable(List<Point2D> pointList) {
			super();

			this.pointList = pointList;
		}

		public long getSizeInBytes() {
			return pointList.size() * 8;
		}
	}
}
