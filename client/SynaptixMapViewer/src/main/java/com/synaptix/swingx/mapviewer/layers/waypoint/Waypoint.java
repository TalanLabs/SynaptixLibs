package com.synaptix.swingx.mapviewer.layers.waypoint;

import org.jdesktop.beans.AbstractBean;
import org.jdesktop.swingx.mapviewer.GeoPosition;

import com.synaptix.swingx.mapviewer.layers.Highlightable;
import com.synaptix.swingx.mapviewer.layers.Selectable;

public class Waypoint extends AbstractBean implements Highlightable, Selectable {

	private GeoPosition position;

	private boolean highlighted;

	private boolean highlightable;

	private boolean selected;

	private boolean selectable;

	public Waypoint() {
		this(new GeoPosition(0, 0));
	}

	public Waypoint(double latitude, double longitude) {
		this(new GeoPosition(latitude, longitude));
	}

	public Waypoint(GeoPosition coord) {
		super();

		this.position = coord;

		this.highlighted = false;
		this.highlightable = true;

		this.selected = false;
		this.selectable = true;
	}

	public GeoPosition getPosition() {
		return position;
	}

	public void setPosition(GeoPosition coordinate) {
		GeoPosition old = getPosition();
		this.position = coordinate;
		firePropertyChange("position", old, getPosition());
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	@Override
	public boolean isHighlightable() {
		return highlightable;
	}

	public void setHighlightable(boolean highlightable) {
		this.highlightable = highlightable;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}
}
