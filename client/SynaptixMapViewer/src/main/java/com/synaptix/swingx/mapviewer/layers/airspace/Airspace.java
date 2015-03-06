package com.synaptix.swingx.mapviewer.layers.airspace;

import java.awt.Graphics2D;
import java.awt.Point;

import org.jdesktop.swingx.mapviewer.DrawContext;

import com.synaptix.swingx.mapviewer.layers.AirspacesLayer;

public interface Airspace {

	public boolean isVisible();

	public void setVisible(boolean visible);

	public void pick(DrawContext dc, Point point, AirspacesLayer airspacesLayer);

	public void paint(Graphics2D g, DrawContext dc,
			AirspacesLayer airspacesLayer);

}
