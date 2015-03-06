package org.jdesktop.swingx.mapviewer;

import java.awt.Point;

import org.jdesktop.swingx.mapviewer.layers.Layer;

public class PickedObject {

	private Layer layer;

	private Object userObject;

	private Point pickPoint;

	public PickedObject(Point pickPoint, Object userObject) {
		super();

		this.userObject = userObject;
		this.pickPoint = pickPoint;
	}

	public Point getPickPoint() {
		return pickPoint;
	}

	public Object getObject() {
		return userObject;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	public Layer getLayer() {
		return layer;
	}
}
