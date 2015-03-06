package com.synaptix.swingx.mapviewer.layers.render;

import java.awt.Graphics2D;
import java.awt.Point;

import org.jdesktop.swingx.mapviewer.DrawContext;

public interface Renderable {

	public void render(Graphics2D g, DrawContext dc);

	public boolean isPick(DrawContext dc, Point point);

}
