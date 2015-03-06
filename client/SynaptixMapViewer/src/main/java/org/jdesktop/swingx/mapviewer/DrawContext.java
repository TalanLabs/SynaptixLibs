package org.jdesktop.swingx.mapviewer;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import org.jdesktop.swingx.mapviewer.cache.MemoryCache;

public interface DrawContext {

	public int getDrawableWidth();

	public int getDrawableHeight();

	public Rectangle getViewportBounds();

	public double getRealZoom();

	public PickedObjectList getPickedObjects();

	public void addPickedObjects(PickedObjectList pickedObjects);

	public void addPickedObject(PickedObject pickedObject);

	public Point getPickPoint();

	public boolean isPickingMode();

	public MemoryCache getMemoryCache();

	public TileFactory getTileFactory();

	public GeoPosition getCenterPosition();

	public double computePixelSize();

	/**
	 * Converti un geo en point selon le composant
	 * 
	 * @param pos
	 * @return
	 */
	public Point2D convertGeoPositionToPoint(GeoPosition pos);

	/**
	 * Converti un point du composant en geo
	 * 
	 * @param pt
	 * @return
	 */
	public GeoPosition convertPointToGeoPosition(Point2D pt);
}
