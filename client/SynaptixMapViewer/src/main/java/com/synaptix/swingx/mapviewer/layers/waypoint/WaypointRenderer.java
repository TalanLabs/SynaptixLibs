package com.synaptix.swingx.mapviewer.layers.waypoint;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.jdesktop.swingx.mapviewer.DrawContext;

import com.synaptix.swingx.mapviewer.layers.WaypointsLayer;

public interface WaypointRenderer {

	public boolean paintWaypoint(Graphics2D g, DrawContext dc, WaypointsLayer waypointLayer, Waypoint waypoint);

	public Rectangle getBoundingBox(DrawContext dc, WaypointsLayer waypointLayer, Waypoint waypoint);

}
