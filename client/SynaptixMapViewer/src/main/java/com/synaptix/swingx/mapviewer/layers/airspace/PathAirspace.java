package com.synaptix.swingx.mapviewer.layers.airspace;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.cache.Cacheable;
import org.jdesktop.swingx.mapviewer.util.CacheKey;

import com.synaptix.swingx.mapviewer.layers.AirspacesLayer;
import com.synaptix.swingx.mapviewer.util.Vec2;

public class PathAirspace extends AbstractShapeAirspace {

	private List<GeoPosition> locations;

	private Path2D path2d = new Path2D.Double();

	private boolean showArrow;

	private double maxScreenSize = 10;

	private double tolerance = 5;

	private double toleranceArrow = 50;

	private boolean detailOfZoom;

	public PathAirspace(List<GeoPosition> locations) {
		this(locations, true);
	}

	public PathAirspace(List<GeoPosition> locations, boolean detailOfZoom) {
		super();

		this.detailOfZoom = detailOfZoom;
		setLocations(locations);
	}

	public List<GeoPosition> getLocations() {
		return locations;
	}

	public void setLocations(List<GeoPosition> locations) {
		if (locations == null || locations.size() < 2) {
			throw new IllegalArgumentException("location size >= 2");
		}
		this.locations = locations;
	}

	public boolean isShowArrow() {
		return showArrow;
	}

	public void setShowArrow(boolean showArrow) {
		this.showArrow = showArrow;
	}

	public double getMaxScreenSize() {
		return maxScreenSize;
	}

	public void setMaxScreenSize(double maxScreenSize) {
		this.maxScreenSize = maxScreenSize;
	}

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	public double getTolerance() {
		return tolerance;
	}

	public void setToleranceArrow(double toleranceArrow) {
		this.toleranceArrow = toleranceArrow;
	}

	public double getToleranceArrow() {
		return toleranceArrow;
	}

	// TODO ameliorer le cache pour faire plus où moins de point selon le niveau
	// de zoom
	private MyGeometryCachable getGeometryCachable(DrawContext dc) {
		CacheKey key = new CacheKey(PathAirspace.class, "pathAirspace", locations, detailOfZoom);
		MyGeometryCachable wc = (MyGeometryCachable) dc.getMemoryCache().getObject(key);
		if (wc == null) {
			List<Vec2> vecList = new ArrayList<Vec2>();
			for (GeoPosition gp : locations) {
				Point2D p1 = dc.getTileFactory().geoToPixel(gp, dc.getTileFactory().getInfo().getMinimumZoomLevel());
				vecList.add(new Vec2(p1.getX(), p1.getY()));
			}

			List<Vec2> normalVecList = new ArrayList<Vec2>();
			List<Vec2> centerVecList = new ArrayList<Vec2>();
			List<Vec2> perpVecList = new ArrayList<Vec2>();
			List<Double> distanceList = new ArrayList<Double>();
			for (int i = 0; i < vecList.size() - 1; i++) {
				Vec2 vecA = vecList.get(i);
				Vec2 vecB = vecList.get(i + 1);

				Vec2 vecC = vecA.add2(vecB).multiply2(0.5);
				centerVecList.add(vecC);

				Vec2 normalAB = vecA.subtract2(vecB).normalize2();
				normalVecList.add(normalAB);

				perpVecList.add(normalAB.perpendicular());

				distanceList.add(vecA.distanceTo2(vecB));
			}
			wc = new MyGeometryCachable(vecList, centerVecList, normalVecList, perpVecList, distanceList);
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
		Shape res;
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
		path2d.moveTo(gc.vecs.get(0).x * scale, gc.vecs.get(0).y * scale);
		for (int i = 1; i < gc.vecs.size(); i++) {
			path2d.lineTo(gc.vecs.get(i).x * scale, gc.vecs.get(i).y * scale);
		}

		return path2d;
	}

	// TODO ne mettre que les points visible
	private Shape createAdaptiveShape(DrawContext dc) {
		MyGeometryCachable gc = getGeometryCachable(dc);

		path2d.reset();
		if (gc.vecs.size() > 1) {
			double scale = getScale(dc);

			Rectangle rect = dc.getViewportBounds();

			double lastx, lasty;
			double x, y;
			double distance;
			double sumDistance = 0;
			Vec2 v;

			v = gc.vecs.get(0);
			lastx = v.x * scale;
			lasty = v.y * scale;

			int i = 0;
			int size = gc.vecs.size();
			boolean first = true;
			while (i < size) {
				v = gc.vecs.get(i);
				x = v.x * scale;
				y = v.y * scale;
				// On cherche le premier point qui rentre
				if (first) {
					// Si premier point dans le rect
					if (rect.contains(x, y)) {
						path2d.moveTo(lastx, lasty);
						path2d.lineTo(x, y);
						first = false;
					} else {
						lastx = x;
						lasty = y;
					}
				} else {
					distance = gc.distances.get(i - 1) * scale;
					sumDistance += distance;
					if (sumDistance > tolerance || i == size - 1) {
						path2d.lineTo(x, y);
						if (!rect.contains(x, y)) {
							first = true;
							lastx = x;
							lasty = y;
						}
						sumDistance = 0;
					}
				}
				i++;
			}
		}
		return path2d;
	}

	@Override
	public void paint(Graphics2D g, DrawContext dc, AirspacesLayer airspacesLayer) {
		if (detailOfZoom) {
			paintAdaptive(g, dc, airspacesLayer);
		} else {
			paintNormal(g, dc, airspacesLayer);
		}
	}

	private void paintNormal(Graphics2D g, DrawContext dc, AirspacesLayer airspacesLayer) {
		super.paint(g, dc, airspacesLayer);

		if (showArrow) {
			AirspaceAttributes attr;
			if (isHighlighted() && getHighlightAirspaceAttributes() != null) {
				attr = getHighlightAirspaceAttributes();
			} else {
				attr = getAirspaceAttributes();
			}
			if (attr != null) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.translate(-dc.getViewportBounds().getX(), -dc.getViewportBounds().getY());

				MyGeometryCachable gc = getGeometryCachable(dc);
				double scale = getScale(dc);

				double vcX, vcY, noX, noY, peX, peY;
				double size;

				path2d.reset();

				for (int i = 1; i < gc.vecs.size(); i++) {
					size = gc.distances.get(i - 1) * scale;
					size = size < maxScreenSize ? size / 4.0 : maxScreenSize;

					vcX = gc.centerVecs.get(i - 1).x * scale;
					vcY = gc.centerVecs.get(i - 1).y * scale;
					noX = gc.normalVecs.get(i - 1).x * size;
					noY = gc.normalVecs.get(i - 1).y * size;
					peX = gc.perpVecs.get(i - 1).x * size;
					peY = gc.perpVecs.get(i - 1).y * size;

					path2d.moveTo(vcX, vcY);
					path2d.lineTo(vcX + (noX + peX), vcY + (noY + peY));
					path2d.moveTo(vcX, vcY);
					path2d.lineTo(vcX + (noX - peX), vcY + (noY - peY));
				}

				g2.setColor(attr.getOutlineColor());
				g2.setStroke(new BasicStroke(attr.getOutlineStroke().getLineWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g2.draw(path2d);

				g2.dispose();
			}
		}
	}

	private void paintAdaptive(Graphics2D g, DrawContext dc, AirspacesLayer airspacesLayer) {
		super.paint(g, dc, airspacesLayer);

		if (showArrow) {
			AirspaceAttributes attr;
			if (isHighlighted() && getHighlightAirspaceAttributes() != null) {
				attr = getHighlightAirspaceAttributes();
			} else {
				attr = getAirspaceAttributes();
			}
			if (attr != null) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.translate(-dc.getViewportBounds().getX(), -dc.getViewportBounds().getY());

				MyGeometryCachable gc = getGeometryCachable(dc);

				path2d.reset();
				if (gc.vecs.size() > 1) {
					double scale = getScale(dc);

					Rectangle rect = dc.getViewportBounds();

					double vcX, vcY, noX, noY, peX, peY;
					double distance;
					double sumDistance = 0;
					double nbPixel;
					Vec2 v, cv, nv, pv;
					double x, y;

					int i = 0;
					int size = gc.vecs.size();
					boolean first = true;
					while (i < size) {
						v = gc.vecs.get(i);
						x = v.x * scale;
						y = v.y * scale;
						// On cherche le premier point qui rentre
						if (first) {
							if (rect.contains(x, y)) {
								first = false;
							}
						} else {
							distance = gc.distances.get(i - 1) * scale;
							sumDistance += distance;
							if (sumDistance > 50 || i == size - 1) {
								nbPixel = sumDistance < maxScreenSize ? sumDistance / 4.0 : maxScreenSize;

								cv = gc.centerVecs.get(i - 1);
								nv = gc.normalVecs.get(i - 1);
								pv = gc.perpVecs.get(i - 1);
								vcX = cv.x * scale;
								vcY = cv.y * scale;
								noX = nv.x * nbPixel;
								noY = nv.y * nbPixel;
								peX = pv.x * nbPixel;
								peY = pv.y * nbPixel;

								path2d.moveTo(vcX, vcY);
								path2d.lineTo(vcX + (noX + peX), vcY + (noY + peY));
								path2d.moveTo(vcX, vcY);
								path2d.lineTo(vcX + (noX - peX), vcY + (noY - peY));

								if (!rect.contains(x, y)) {
									first = true;
								}
								sumDistance = 0;
							}
						}
						i++;
					}
				}
				g2.setColor(attr.getOutlineColor());
				g2.setStroke(new BasicStroke(attr.getOutlineStroke().getLineWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g2.draw(path2d);

				g2.dispose();
			}
		}
	}

	@Override
	protected void pick(DrawContext dc, Point point, AirspacesLayer airspacesLayer, Shape shape) {
		BasicStroke bs = new BasicStroke(getAirspaceAttributes().getOutlineStroke().getLineWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		super.pick(dc, point, airspacesLayer, bs.createStrokedShape(shape));
	}

	@Override
	protected Shape getSelectedShape(DrawContext dc, Shape shape) {
		BasicStroke bs = new BasicStroke(getAirspaceAttributes().getOutlineStroke().getLineWidth() + 5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		return bs.createStrokedShape(shape);
	}

	private static final class MyGeometryCachable implements Cacheable {

		public final List<Vec2> vecs;

		public final List<Vec2> centerVecs;

		public final List<Vec2> normalVecs;

		public final List<Vec2> perpVecs;

		public final List<Double> distances;

		public MyGeometryCachable(List<Vec2> vecs, List<Vec2> centerVecs, List<Vec2> normalVecs, List<Vec2> perpVecs, List<Double> distances) {
			super();

			this.vecs = vecs;
			this.centerVecs = centerVecs;
			this.normalVecs = normalVecs;
			this.perpVecs = perpVecs;
			this.distances = distances;
		}

		public long getSizeInBytes() {
			return (vecs.size() + centerVecs.size() + normalVecs.size() + perpVecs.size()) * 8 * 2 + distances.size() * 8;
		}
	}
}
