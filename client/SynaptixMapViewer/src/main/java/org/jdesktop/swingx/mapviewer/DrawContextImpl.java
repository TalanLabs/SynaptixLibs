package org.jdesktop.swingx.mapviewer;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.cache.MemoryCache;

public class DrawContextImpl implements DrawContext {

	private JXMapViewer mapViewer;

	private Rectangle viewportBounds;

	private PickedObjectList pickedObjectList;

	private Point pickPoint;

	private boolean pickingMode;

	public DrawContextImpl(JXMapViewer mapViewer) {
		this.mapViewer = mapViewer;
	}

	@Override
	public int getDrawableWidth() {
		return mapViewer.getWidth();
	}

	@Override
	public int getDrawableHeight() {
		return mapViewer.getHeight();
	}

	public void setViewportBounds(Rectangle viewportBounds) {
		this.viewportBounds = viewportBounds;
	}

	@Override
	public Rectangle getViewportBounds() {
		return viewportBounds;
	}

	@Override
	public double getRealZoom() {
		return mapViewer.getRealZoom();
	}

	public void setPickedObjectList(PickedObjectList pickedObjectList) {
		this.pickedObjectList = pickedObjectList;
	}

	@Override
	public PickedObjectList getPickedObjects() {
		return pickedObjectList;
	}

	@Override
	public void addPickedObjects(PickedObjectList pickedObjects) {
		pickedObjectList.addAll(pickedObjects);
	}

	@Override
	public void addPickedObject(PickedObject pickedObject) {
		pickedObjectList.add(pickedObject);
	}

	public void setPickPoint(Point pickPoint) {
		this.pickPoint = pickPoint;
	}

	@Override
	public Point getPickPoint() {
		return pickPoint;
	}

	public void setPickingMode(boolean pickingMode) {
		this.pickingMode = pickingMode;
	}

	@Override
	public boolean isPickingMode() {
		return pickingMode;
	}

	@Override
	public MemoryCache getMemoryCache() {
		return mapViewer.getMemoryCache();
	}

	@Override
	public TileFactory getTileFactory() {
		return mapViewer.getTileFactory();
	}

	@Override
	public GeoPosition getCenterPosition() {
		return mapViewer.getCenterPosition();
	}

	@Override
	public Point2D convertGeoPositionToPoint(GeoPosition pos) {
		return mapViewer.convertGeoPositionToPoint(pos);
	}

	@Override
	public GeoPosition convertPointToGeoPosition(Point2D pt) {
		return mapViewer.convertPointToGeoPosition(pt);
	}

	@Override
	public double computePixelSize() {
		return mapViewer.computePixelSize();
	}
}
