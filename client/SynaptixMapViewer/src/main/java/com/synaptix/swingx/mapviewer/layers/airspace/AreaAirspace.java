package com.synaptix.swingx.mapviewer.layers.airspace;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.cache.Cacheable;
import org.jdesktop.swingx.mapviewer.util.CacheKey;

import com.synaptix.swingx.mapviewer.util.Vec2;

public class AreaAirspace extends AbstractShapeAirspace {

	private List<GeoPosition> locations;

	private List<Integer> groups;

	private Path2D path2d = new Path2D.Double();

	private double tolerance = 5;

	private boolean detailOfZoom;

	public AreaAirspace(List<GeoPosition> locations) {
		this(locations, null);
	}

	public AreaAirspace(List<GeoPosition> locations, List<Integer> groups) {
		super();

		this.detailOfZoom = true;

		setLocations(locations, groups);
	}

	public List<GeoPosition> getLocations() {
		return locations;
	}

	public List<Integer> getGroups() {
		return groups;
	}

	public void setLocations(List<GeoPosition> locations, List<Integer> groups) {
		if (locations == null || locations.size() < 3) {
			throw new IllegalArgumentException("location size >= 3");
		}
		this.locations = locations;
		this.groups = groups;
	}

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	public double getTolerance() {
		return tolerance;
	}

	public void setDetailOfZoom(boolean detailOfZoom) {
		this.detailOfZoom = detailOfZoom;
	}

	public boolean isDetailOfZoom() {
		return detailOfZoom;
	}

	private MyGeometryCachable getGeometryCachable(DrawContext dc) {
		CacheKey key = new CacheKey(PathAirspace.class, "pathAirspace", locations);
		MyGeometryCachable wc = (MyGeometryCachable) dc.getMemoryCache().getObject(key);
		if (wc == null) {
			List<List<Vec2>> vecss = new ArrayList<List<Vec2>>();
			List<Vec2> currents = new ArrayList<Vec2>();
			int i = 0;
			int j = 0;
			for (GeoPosition gp : locations) {
				Point2D p1 = dc.getTileFactory().geoToPixel(gp, dc.getTileFactory().getInfo().getMinimumZoomLevel());
				currents.add(new Vec2(p1.getX(), p1.getY()));

				i++;

				if (groups != null && j < groups.size() && groups.get(j) == i) {
					vecss.add(currents);
					currents = new ArrayList<Vec2>();
					j++;
				}
			}
			vecss.add(currents);

			List<List<Double>> distancess = new ArrayList<List<Double>>();
			for (List<Vec2> vecs : vecss) {
				List<Double> distances = new ArrayList<Double>();
				for (int k = 0; k < vecs.size() - 1; k++) {
					Vec2 vecA = vecs.get(k);
					Vec2 vecB = vecs.get(k + 1);

					distances.add(vecA.distanceTo2(vecB));
				}
				distancess.add(distances);
			}
			wc = new MyGeometryCachable(vecss, distancess);
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
		Shape res = null;
		if (detailOfZoom) {
			res = createAdaptiveShape(dc);
		} else {
			res = createNormalShape(dc);
		}
		return res;
	}

	private Shape createNormalShape(DrawContext dc) {
		MyGeometryCachable gc = getGeometryCachable(dc);
		double scale = getScale(dc);

		path2d.reset();
		for (List<Vec2> vecs : gc.vecss) {
			path2d.moveTo(vecs.get(0).x * scale, vecs.get(0).y * scale);
			for (int i = 1; i < vecs.size(); i++) {
				path2d.lineTo(vecs.get(i).x * scale, vecs.get(i).y * scale);
			}
		}

		return path2d;
	}

	// TODO ne mettre que les points visible
	private Shape createAdaptiveShape(DrawContext dc) {
		MyGeometryCachable gc = getGeometryCachable(dc);

		path2d.reset();
		int j = 0;
		for (List<Vec2> vecs : gc.vecss) {
			List<Double> distances = gc.distancess.get(j);
			if (vecs.size() > 1) {
				double scale = getScale(dc);

				// Rectangle rect = dc.getViewportBounds();

				double lastx, lasty;
				double x, y;
				double distance;
				double sumDistance = 0;
				Vec2 v;

				v = vecs.get(0);
				lastx = v.x * scale;
				lasty = v.y * scale;

				int i = 0;
				int size = vecs.size();
				boolean first = true;
				while (i < size) {
					v = vecs.get(i);
					x = v.x * scale;
					y = v.y * scale;
					// On cherche le premier point qui rentre
					if (first) {
						// Si premier point dans le rect
						// if (rect.contains(x, y)) {
						path2d.moveTo(lastx, lasty);
						path2d.lineTo(x, y);
						first = false;
						// } else {
						// lastx = x;
						// lasty = y;
						// }
					} else {
						distance = distances.get(i - 1) * scale;
						sumDistance += distance;
						if (sumDistance > tolerance || i == size - 1) {
							path2d.lineTo(x, y);
							// if (!rect.contains(x, y)) {
							// first = true;
							// lastx = x;
							// lasty = y;
							// }
							sumDistance = 0;
						}
					}
					i++;
				}
			}
			j++;
		}
		return path2d;
	}

	private static final class MyGeometryCachable implements Cacheable {

		public final List<List<Vec2>> vecss;

		public final List<List<Double>> distancess;

		public MyGeometryCachable(List<List<Vec2>> vecss, List<List<Double>> distancess) {
			super();

			this.vecss = vecss;
			this.distancess = distancess;
		}

		public long getSizeInBytes() {
			int size = 0;
			for (int i = 0; i < vecss.size(); i++) {
				List<Vec2> vecs = vecss.get(i);
				List<Double> distances = distancess.get(i);
				size += (vecs.size()) * 8 * 2 + distances.size() * 8;
			}
			return size;
		}
	}
}
