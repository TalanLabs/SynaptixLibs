package com.synaptix.swingx.mapviewer.layers;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.swingx.mapviewer.DrawContext;
import org.jdesktop.swingx.mapviewer.layers.AbstractLayer;

import com.synaptix.swingx.mapviewer.layers.airspace.Airspace;

public class AirspacesLayer extends AbstractLayer {

	private List<Airspace> airspaces;

	public AirspacesLayer() {
		super();

		this.setAntialiasing(true);
		this.setCacheable(false);

		this.setPickEnabled(true);

		this.airspaces = new ArrayList<Airspace>();
	}

	public void addAirspace(Airspace airspace) {
		this.airspaces.add(airspace);
	}

	public void removeAirspace(Airspace airspace) {
		this.airspaces.remove(airspace);
	}

	public void removeAllAirspaces() {
		this.airspaces.clear();
	}

	@Override
	protected void doPick(DrawContext dc, Point point) {
		for (Airspace airspace : airspaces) {
			if (airspace.isVisible()) {
				airspace.pick(dc, point, this);
			}
		}
	}

	@Override
	protected void doPaint(Graphics2D g, DrawContext dc) {
		for (Airspace airspace : airspaces) {
			if (airspace.isVisible()) {
				airspace.paint(g, dc, this);
			}
		}
	}

}
