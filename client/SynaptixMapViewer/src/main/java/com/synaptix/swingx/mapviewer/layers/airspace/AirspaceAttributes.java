package com.synaptix.swingx.mapviewer.layers.airspace;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public interface AirspaceAttributes {

	public boolean isDrawInterior();

	public void setDrawInterior(boolean drawInterior);

	public boolean isDrawOutline();

	public void setDrawOutline(boolean drawOutline);

	public Color getColor();

	public void setColor(Color color);

	public Color getOutlineColor();

	public void setOutlineColor(Color outlineColor);

	public void applyInterior(Graphics2D g);

	public void applyOutline(Graphics2D g);

	public BasicStroke getOutlineStroke();
	
	public void setOutlineStroke(BasicStroke outlineStroke);

}