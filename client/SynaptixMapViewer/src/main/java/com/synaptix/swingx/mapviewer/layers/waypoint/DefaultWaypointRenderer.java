package com.synaptix.swingx.mapviewer.layers.waypoint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import org.jdesktop.swingx.mapviewer.DrawContext;

import com.synaptix.swingx.mapviewer.layers.WaypointsLayer;

public class DefaultWaypointRenderer implements WaypointRenderer {

	protected Rectangle boundingBox = new Rectangle(-10, -10, 20, 20);

	protected Color selectedColor = new Color(0, 0, 255, 128);

	protected Color borderSelectedColor = new Color(0, 0, 255, 192);

	protected Image img = null;

	public DefaultWaypointRenderer() {
		try {
			img = new ImageIcon(DefaultWaypointRenderer.class.getResource("standard_waypoint.png")).getImage();
		} catch (Exception ex) {
			System.out.println("couldn't read standard_waypoint.png");
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

	@Override
	public boolean paintWaypoint(Graphics2D g, DrawContext dc, WaypointsLayer waypointLayer, Waypoint waypoint) {
		if (img != null) {
			g.drawImage(img, -img.getWidth(null) / 2, -img.getHeight(null), null);
		} else {
			g.setStroke(new BasicStroke(3f));
			g.setColor(Color.BLUE);
			g.drawOval(-10, -10, 20, 20);
			g.setStroke(new BasicStroke(1f));
			g.drawLine(-10, 0, 10, 0);
			g.drawLine(0, -10, 0, 10);
		}

		if (waypoint.isSelected()) {
			g.setColor(selectedColor);
			g.fillRect(-10, -10, 20, 20);

			g.setColor(borderSelectedColor);
			g.drawRect(-10, -10, 20, 20);
		}
		return false;
	}

	@Override
	public Rectangle getBoundingBox(DrawContext dc, WaypointsLayer waypointLayer, Waypoint waypoint) {
		return boundingBox;
	}
}
